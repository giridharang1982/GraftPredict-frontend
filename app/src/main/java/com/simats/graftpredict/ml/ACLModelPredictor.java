package com.simats.graftpredict.ml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.net.Uri;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;

public class ACLModelPredictor {

    private Interpreter mrnetInterpreter;
    private Interpreter kneeMRIInterpreter;
    private final float MRNET_THRESHOLD = 0.429860f; // Exact threshold from Python code
    private final int TARGET_DEPTH = 30;
    private final int TARGET_WIDTH = 256;
    private final int TARGET_HEIGHT = 256;

    // Label mappings matching Python app.py
    private final String[] mrnetLabels = { "Healthy", "ACL Tear" };
    private final String[] kneeMRILabels = { "Healthy", "Partial ACL Tear", "Complete ACL Tear" };

    // Healthy, Partial ACL Tear, Complete ACL Tear

    // NOTE: Interpreters are lazily loaded only after MRI validation succeeds.
    // This prevents unnecessary initialization (and avoids loading models when
    // input is invalid). See ensureModelsLoaded(...) below.
    private final Context appContext;

    public ACLModelPredictor(Context context) {
        // Store application context for later use in ensureModelsLoaded
        this.appContext = context.getApplicationContext();
        mrnetInterpreter = null;
        kneeMRIInterpreter = null;
    }

    private MappedByteBuffer loadModelFile(Context context, String modelName) throws IOException {
        FileInputStream fis = new FileInputStream(context.getAssets().openFd(modelName).getFileDescriptor());
        FileChannel fc = fis.getChannel();
        return fc.map(FileChannel.MapMode.READ_ONLY,
                context.getAssets().openFd(modelName).getStartOffset(),
                context.getAssets().openFd(modelName).getDeclaredLength());
    }

    // Lazily load TensorFlow Lite interpreters only after validation succeeds.
    private synchronized void ensureModelsLoaded(Context context) throws IOException {
        if (mrnetInterpreter != null && kneeMRIInterpreter != null)
            return;

        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(4);

        // Try to use GPU delegate if available
        try {
            Class<?> gpuDelegateClass = Class.forName("org.tensorflow.lite.gpu.GpuDelegate");
            Object gpuDelegate = gpuDelegateClass.getConstructor().newInstance();
            // Use reflection to call addDelegate
            java.lang.reflect.Method addDelegateMethod = Interpreter.Options.class.getMethod("addDelegate",
                    Object.class);
            addDelegateMethod.invoke(options, gpuDelegate);
        } catch (Exception e) {
            // GPU not available or not compatible, will use CPU with threads
        }

        mrnetInterpreter = new Interpreter(loadModelFile(context, "MRNet_Model3.tflite"), options);
        kneeMRIInterpreter = new Interpreter(loadModelFile(context, "kneeMRI_Model6.tflite"), options);
    }

    public String predictFromImages(List<Uri> imageUris, Context context) throws IOException {
        if (imageUris == null || imageUris.isEmpty()) {
            throw new IllegalArgumentException("No images provided");
        }

        // New feature: detect mixed image types early and fail fast with a clear
        // message
        // If the user uploaded differing image types (e.g., jpg + png), we must return
        // "invalid images of different types (jpg and png)" and not proceed.
        String mixedTypesMessage = detectMixedImageTypes(imageUris, context);
        if (mixedTypesMessage != null) {
            // Ensure the UI prints the message on a new line as requested
            return "\n" + mixedTypesMessage;
        }

        // 1) Load bitmaps and convert to grayscale (keep original sizes for now)
        List<Bitmap> bitmaps = new ArrayList<>();
        for (Uri uri : imageUris) {
            Bitmap bmp = loadBitmap(uri, context);
            if (bmp != null) {
                Bitmap gray = convertToGrayscale(bmp);
                // Recycle original if a distinct bitmap was created
                if (gray != bmp) {
                    bmp.recycle();
                }
                bitmaps.add(gray);
            }
        }

        if (bitmaps.isEmpty()) {
            throw new IOException("Failed to load any images");
        }

        // 2) Resize all slices to a common shape (median width & height) BEFORE
        // stacking
        // This mirrors Python's resize_slices_to_common_shape()
        int targetHeight = medianInt(getHeights(bitmaps));
        int targetWidth = medianInt(getWidths(bitmaps));

        List<Bitmap> resizedBitmaps = new ArrayList<>();
        for (Bitmap b : bitmaps) {
            Bitmap r = Bitmap.createScaledBitmap(b, targetWidth, targetHeight, true);
            // Recycle original to avoid memory pressure
            if (r != b)
                b.recycle();
            resizedBitmaps.add(r);
        }

        // 3) MRI Validation gate: if this fails, we must return "image invalid" and
        // NOT initialize or invoke any TensorFlow Lite interpreters.
        // See isValidMRIVolume(...) for the specific checks implemented (count,
        // grayscale, std range, slice-to-slice correlation). This mirrors the
        // recent Python changes and prevents running inference on non-MRI images.
        boolean valid = isValidMRIVolume(resizedBitmaps);
        if (!valid) {
            // Ensure we do not load interpreters or run inference
            for (Bitmap b : resizedBitmaps)
                b.recycle();
            // Prefix with newline per request
            return "\nimage invalid";
        }

        // 4) Convert resized grayscale bitmaps to normalized float slices in range
        // [0,1]
        List<float[][]> slices = new ArrayList<>();
        for (Bitmap b : resizedBitmaps) {
            slices.add(convertBitmapToFloatArray(b));
            b.recycle();
        }

        // 5) Stack into 3D volume and proceed with existing preprocessing
        float[][][] mriVolume = stackSlices(slices);

        // Resize depth to target (30 slices)
        mriVolume = resizeDepth(mriVolume);

        // Preprocess matching Python utils.py preprocess_mri function
        // Note: We skip denoise and bias_field_correction as they require heavy
        // libraries. The normalize -> center -> standardize sequence matches the
        // Python code.
        mriVolume = normalizeVolumePixels(mriVolume);
        mriVolume = centerVolumePixels(mriVolume);
        mriVolume = standardizeVolumePixels(mriVolume);

        // Prepare input tensor: [batch=1, depth=30, height=256, width=256, channels=1]
        float[][][][][] inputTensor = createInputTensor(mriVolume);

        // Lazily load models now that validation succeeded
        ensureModelsLoaded(this.appContext);

        // Run inference
        float[][] mrnetOutput = new float[1][1];
        float[][] kneeOutput = new float[1][3];

        mrnetInterpreter.run(inputTensor, mrnetOutput);
        kneeMRIInterpreter.run(inputTensor, kneeOutput);

        // Interpret results matching Python app.py logic
        String res = interpretResults(mrnetOutput[0], kneeOutput[0]);
        // Prefix with newline so it's printed on a new line in the terminal view
        if (res == null)
            return "\n";
        if (res.startsWith("\n"))
            return res;
        return "\n" + res;
    }

    private Bitmap loadBitmap(Uri uri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }

    private Bitmap convertToGrayscale(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bmp, 0, 0, paint);
        return grayBitmap;
    }

    private float[][] convertBitmapToFloatArray(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        float[][] arr = new float[height][width]; // Note: [height][width] not [width][height]

        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                // Extract grayscale value (ARGB format) using ITU-R BT.601 standard weights
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                int gray = (int) (r * 0.299 + g * 0.587 + b * 0.114);
                // Store as float in range [0, 1]
                arr[y][x] = gray / 255.0f;
            }
        }
        return arr;
    }

    // --- MRI validation helpers ---

    // Get heights list from bitmaps
    private List<Integer> getHeights(List<Bitmap> bitmaps) {
        List<Integer> out = new ArrayList<>();
        for (Bitmap b : bitmaps)
            out.add(b.getHeight());
        return out;
    }

    // Get widths list from bitmaps
    private List<Integer> getWidths(List<Bitmap> bitmaps) {
        List<Integer> out = new ArrayList<>();
        for (Bitmap b : bitmaps)
            out.add(b.getWidth());
        return out;
    }

    private int medianInt(List<Integer> values) {
        if (values == null || values.isEmpty())
            return TARGET_HEIGHT; // fallback
        List<Integer> copy = new ArrayList<>(values);
        java.util.Collections.sort(copy);
        int mid = copy.size() / 2;
        if (copy.size() % 2 == 1)
            return copy.get(mid);
        return (copy.get(mid - 1) + copy.get(mid)) / 2;
    }

    // Validate whether the given (already resized & grayscale) slices resemble a
    // real MRI volume. If any check fails, return false. This mirrors the Python
    // 'is_valid_mri_volume' logic: sufficient slices, grayscale, intensity std
    // range, and slice-to-slice correlation.
    private boolean isValidMRIVolume(List<Bitmap> slices) {
        try {
            if (slices == null || slices.size() < 10)
                return false;

            // Convert each slice to a flattened float array in 0..255 scale
            List<float[]> flattened = new ArrayList<>();
            int hw = -1;
            for (Bitmap b : slices) {
                float[] f = convertBitmapToRawFloatArray(b);
                if (hw == -1)
                    hw = f.length;
                else if (f.length != hw)
                    return false;
                flattened.add(f);
            }

            // Compute global std dev across entire volume
            float[] all = new float[flattened.size() * hw];
            int idx = 0;
            for (float[] f : flattened) {
                System.arraycopy(f, 0, all, idx, f.length);
                idx += f.length;
            }

            double std = computeStd(all);
            if (std < 5.0 || std > 80.0)
                return false;

            // Compute slice-to-slice Pearson correlations
            double sumCorr = 0.0;
            int count = 0;
            for (int i = 0; i < flattened.size() - 1; i++) {
                double corr = pearsonCorrelation(flattened.get(i), flattened.get(i + 1));
                if (Double.isNaN(corr))
                    corr = 0.0;
                sumCorr += corr;
                count++;
            }
            double meanCorr = (count > 0) ? (sumCorr / count) : 0.0;
            if (meanCorr < 0.6)
                return false;

            return true;
        } catch (Exception ex) {
            // Any unexpected error -> treat as invalid MRI (fail-safe)
            return false;
        }
    }

    // Convert bitmap to raw float array in 0..255 (flattened row-major)
    private float[] convertBitmapToRawFloatArray(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        float[] out = new float[width * height];
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;
            int gray = (int) (r * 0.299 + g * 0.587 + b * 0.114);
            out[i] = (float) gray;
        }
        return out;
    }

    private double computeStd(float[] arr) {
        double sum = 0.0;
        for (float v : arr)
            sum += v;
        double mean = sum / arr.length;
        double sq = 0.0;
        for (float v : arr) {
            double d = v - mean;
            sq += d * d;
        }
        return Math.sqrt(sq / arr.length);
    }

    private double pearsonCorrelation(float[] a, float[] b) {
        if (a.length != b.length)
            return Double.NaN;
        int n = a.length;
        double sumA = 0.0, sumB = 0.0;
        for (int i = 0; i < n; i++) {
            sumA += a[i];
            sumB += b[i];
        }
        double meanA = sumA / n;
        double meanB = sumB / n;
        double cov = 0.0;
        double sqA = 0.0, sqB = 0.0;
        for (int i = 0; i < n; i++) {
            double da = a[i] - meanA;
            double db = b[i] - meanB;
            cov += da * db;
            sqA += da * da;
            sqB += db * db;
        }
        double denom = Math.sqrt(sqA * sqB);
        if (denom == 0.0)
            return 0.0;
        return cov / denom;
    }

    // --- Detect mixed image types ---
    // Returns null if all images share the same type, otherwise returns the
    // specific error string required by the app (for example:
    // "invalid images of different types (jpg and png)"). This check is run
    // BEFORE any image processing or model loading.
    private String detectMixedImageTypes(List<Uri> uris, Context context) {
        java.util.Set<String> types = new LinkedHashSet<>();
        for (Uri u : uris) {
            String t = getImageTypeFromUri(u, context);
            if (t == null)
                t = "unknown";
            types.add(t.toLowerCase());
        }
        if (types.size() <= 1)
            return null;

        String[] arr = types.toArray(new String[0]);
        String joined;
        if (arr.length == 2)
            joined = arr[0] + " and " + arr[1];
        else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                if (i == arr.length - 1)
                    sb.append("and ").append(arr[i]);
                else
                    sb.append(arr[i]).append(", ");
            }
            joined = sb.toString();
        }
        return "invalid images of different types (" + joined + ")";
    }

    private String getImageTypeFromUri(Uri uri, Context ctx) {
        try {
            android.content.ContentResolver cr = ctx.getContentResolver();
            String mime = cr.getType(uri);
            if (mime != null && mime.startsWith("image/")) {
                String subtype = mime.substring(6).toLowerCase();
                if (subtype.equals("jpeg") || subtype.equals("pjpeg"))
                    return "jpg";
                if (subtype.equals("png"))
                    return "png";
                if (subtype.equals("webp"))
                    return "webp";
                return subtype;
            }
        } catch (Exception ignored) {
        }

        String path = uri.getPath();
        if (path != null) {
            int dot = path.lastIndexOf('.');
            if (dot >= 0 && dot < path.length() - 1) {
                String ext = path.substring(dot + 1).toLowerCase();
                if (ext.equals("jpeg"))
                    ext = "jpg";
                return ext;
            }
        }
        return "unknown";
    }

    private float[][][] stackSlices(List<float[][]> slices) {
        int depth = slices.size();
        int height = slices.get(0).length;
        int width = slices.get(0)[0].length;
        float[][][] volume = new float[depth][height][width];
        for (int i = 0; i < depth; i++) {
            volume[i] = slices.get(i);
        }
        return volume;
    }

    private float[][][] resizeDepth(float[][][] vol) {
        int currentDepth = vol.length;
        if (currentDepth == TARGET_DEPTH) {
            return vol;
        }

        // Linear interpolation for depth resizing
        float[][][] resized = new float[TARGET_DEPTH][TARGET_HEIGHT][TARGET_WIDTH];
        float depthRatio = (float) (currentDepth - 1) / (TARGET_DEPTH - 1);

        for (int z = 0; z < TARGET_DEPTH; z++) {
            float srcZ = z * depthRatio;
            int z0 = (int) Math.floor(srcZ);
            int z1 = Math.min(z0 + 1, currentDepth - 1);
            float t = srcZ - z0;

            // Copy if exact match, otherwise interpolate
            if (t < 0.001f) {
                // Direct copy
                for (int y = 0; y < TARGET_HEIGHT; y++) {
                    System.arraycopy(vol[z0][y], 0, resized[z][y], 0, TARGET_WIDTH);
                }
            } else {
                // Linear interpolation between two slices
                for (int y = 0; y < TARGET_HEIGHT; y++) {
                    for (int x = 0; x < TARGET_WIDTH; x++) {
                        resized[z][y][x] = vol[z0][y][x] * (1 - t) + vol[z1][y][x] * t;
                    }
                }
            }
        }
        return resized;
    }

    // normalizeVolumePixels: Normalize the volume pixels to the range [0, 1]
    private float[][][] normalizeVolumePixels(float[][][] vol) {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        // Find min and max
        for (float[][] slice : vol) {
            for (float[] row : slice) {
                for (float val : row) {
                    if (val < min)
                        min = val;
                    if (val > max)
                        max = val;
                }
            }
        }

        // Normalize to [0, 1]
        float range = max - min;
        if (range == 0) {
            // All values are the same, set to 0
            for (int z = 0; z < vol.length; z++) {
                for (int y = 0; y < vol[0].length; y++) {
                    for (int x = 0; x < vol[0][0].length; x++) {
                        vol[z][y][x] = 0f;
                    }
                }
            }
        } else {
            for (int z = 0; z < vol.length; z++) {
                for (int y = 0; y < vol[0].length; y++) {
                    for (int x = 0; x < vol[0][0].length; x++) {
                        vol[z][y][x] = (vol[z][y][x] - min) / range;
                    }
                }
            }
        }
        return vol;
    }

    // centerVolumePixels: Center the data by subtracting the mean
    private float[][][] centerVolumePixels(float[][][] vol) {
        float sum = 0f;
        int count = vol.length * vol[0].length * vol[0][0].length;

        for (float[][] slice : vol) {
            for (float[] row : slice) {
                for (float val : row) {
                    sum += val;
                }
            }
        }

        float mean = sum / count;

        // Center by subtracting mean
        for (int z = 0; z < vol.length; z++) {
            for (int y = 0; y < vol[0].length; y++) {
                for (int x = 0; x < vol[0][0].length; x++) {
                    vol[z][y][x] = vol[z][y][x] - mean;
                }
            }
        }
        return vol;
    }

    // standardizeVolumePixels: Standardise the data (normalize by std deviation)
    private float[][][] standardizeVolumePixels(float[][][] vol) {
        float sum = 0f;
        int count = vol.length * vol[0].length * vol[0][0].length;

        // Calculate mean
        for (float[][] slice : vol) {
            for (float[] row : slice) {
                for (float val : row) {
                    sum += val;
                }
            }
        }
        float mean = sum / count;

        // Calculate standard deviation
        float varianceSum = 0f;
        for (float[][] slice : vol) {
            for (float[] row : slice) {
                for (float val : row) {
                    float diff = val - mean;
                    varianceSum += diff * diff;
                }
            }
        }
        float std = (float) Math.sqrt(varianceSum / count);

        // Standardize: divide by std (avoid division by zero)
        if (std > 0.0001f) {
            for (int z = 0; z < vol.length; z++) {
                for (int y = 0; y < vol[0].length; y++) {
                    for (int x = 0; x < vol[0][0].length; x++) {
                        vol[z][y][x] = vol[z][y][x] / std;
                    }
                }
            }
        }
        return vol;
    }

    private float[][][][][] createInputTensor(float[][][] vol) {
        // Input shape: [batch=1, depth=30, height=256, width=256, channels=1]
        // Note: Python models expect (depth, width, height) but TensorFlow uses (depth,
        // height, width)
        // Our vol[z][y][x] needs to map to tensor[batch][z][y][x][channel]
        float[][][][][] tensor = new float[1][TARGET_DEPTH][TARGET_HEIGHT][TARGET_WIDTH][1];
        for (int z = 0; z < TARGET_DEPTH; z++) {
            for (int y = 0; y < TARGET_HEIGHT; y++) {
                for (int x = 0; x < TARGET_WIDTH; x++) {
                    tensor[0][z][y][x][0] = vol[z][y][x];
                }
            }
        }
        return tensor;
    }

    // Interpret results matching Python app.py logic exactly
    private String interpretResults(float[] mrnetOutput, float[] kneeOutput) {
        float mrnetProb = mrnetOutput[0];
        int mrnetLabel = (mrnetProb >= MRNET_THRESHOLD) ? 1 : 0;
        int kneemriLabel = argmax(kneeOutput);

        String mrnetLabelStr = mrnetLabels[mrnetLabel];
        String kneemriLabelStr = kneeMRILabels[kneemriLabel];

        // Logic matching Python app.py lines 63-73
        if (mrnetLabel == 1 && kneemriLabel == 0) {
            // MRNet predicts tear, KneeMRI predicts healthy
            if (mrnetProb > kneeOutput[kneemriLabel]) {
                return "ACL tear prediction: " + mrnetLabelStr + "\n\n" +
                        "Warning: Possible ACL tear, degree of tear uncertain.";
            }
        } else if (mrnetLabel == 0 && kneemriLabel > 0) {
            // MRNet predicts healthy, KneeMRI predicts tear
            if (mrnetProb < kneeOutput[kneemriLabel]) {
                return "Prediction of ACL tear degree: " + kneemriLabelStr + "\n\n" +
                        "Warning: Possibility of ACL tear.";
            }
        }

        // Default case: both predictions are available
        return "ACL tear prediction: " + mrnetLabelStr + "\n\n" +
                "Prediction of ACL tear degree: " + kneemriLabelStr + "\n\n" +
                "MRNet Probability: " + String.format("%.3f", mrnetProb) + "\n" +
                "KneeMRI Probabilities: [" +
                String.format("%.3f", kneeOutput[0]) + ", " +
                String.format("%.3f", kneeOutput[1]) + ", " +
                String.format("%.3f", kneeOutput[2]) + "]";
    }

    private int argmax(float[] arr) {
        int idx = 0;
        float max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                idx = i;
            }
        }
        return idx;
    }

    public void close() {
        try {
            if (mrnetInterpreter != null)
                mrnetInterpreter.close();
        } catch (Exception ignored) {
        }
        try {
            if (kneeMRIInterpreter != null)
                kneeMRIInterpreter.close();
        } catch (Exception ignored) {
        }
    }
}
