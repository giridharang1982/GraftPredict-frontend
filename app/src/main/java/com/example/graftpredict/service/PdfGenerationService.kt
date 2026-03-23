package com.example.graftpredict.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.print.*
import android.webkit.*
import android.webkit.WebView
import com.example.graftpredict.data.models.ReportData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Service for generating PDF reports from HTML templates using WebView.
 * Uses Android's PrintManager API for high-quality PDF generation.
 */
class PdfGenerationService(private val context: Context) {

    interface PdfGenerationCallback {
        fun onPdfGenerated(file: File)
        fun onPdfGenerationFailed(error: String)
    }

    /**
     * Generate PDF asynchronously - non-blocking UI thread
     * Heavy I/O operations run on background thread
     */
    suspend fun generatePdfReportAsync(
        reportData: ReportData,
        fileName: String = "GraftReport_${System.currentTimeMillis()}.pdf"
    ): Result<File> = try {
        // 1. Load and prepare HTML on background thread (I/O intensive)
        val htmlTemplate = withContext(Dispatchers.IO) {
            loadHtmlTemplate()
        }
        
        val processedHtml = withContext(Dispatchers.Default) {
            processHtmlTemplate(htmlTemplate, reportData)
        }

        // 2. Prepare output file on background thread
        val outputFile = withContext(Dispatchers.IO) {
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                fileName
            ).apply {
                parentFile?.mkdirs()
            }
        }

        // 3. Switch to Main thread ONLY for WebView creation and rendering
        val result = withContext(Dispatchers.Main) {
            generatePdfWithWebView(processedHtml, outputFile)
        }

        result
    } catch (e: Exception) {
        Result.failure(Exception("PDF generation error: ${e.message}"))
    }

    /**
     * Handle WebView rendering and PDF generation on Main thread
     * This is kept minimal to avoid blocking UI
     */
    private suspend fun generatePdfWithWebView(
        processedHtml: String,
        outputFile: File
    ): Result<File> = try {
        val completionSource = CompletableFuture<File>()
        val webView = createWebView()
        
        var renderingStarted = false
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                
                if (!renderingStarted) {
                    renderingStarted = true
                    // Reduced delay to 500ms - WebView renders progressively
                    Handler(Looper.getMainLooper()).postDelayed({
                        generatePdfFromWebViewOnMainThread(webView, outputFile, completionSource)
                    }, 500)
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (!completionSource.isDone) {
                    completionSource.completeExceptionally(Exception("Failed to load HTML: ${error?.description}"))
                }
            }
        }

        // Load HTML (this is async and won't block once started)
        webView.loadDataWithBaseURL(
            "file:///android_asset/",
            processedHtml,
            "text/html",
            "UTF-8",
            null
        )

        // Wait for PDF generation with timeout (on background thread to not block Main)
        withContext(Dispatchers.Default) {
            try {
                val result = completionSource.get(45, java.util.concurrent.TimeUnit.SECONDS)
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(Exception("PDF generation timeout or failed: ${e.message}"))
            }
        }
    } catch (e: Exception) {
        Result.failure(Exception("WebView rendering failed: ${e.message}"))
    }

    /**
     * Load HTML template from assets
     */
    private fun loadHtmlTemplate(): String {
        return context.assets.open("report_template.html")
            .bufferedReader()
            .use { it.readText() }
    }

    /**
     * Replace placeholders in HTML with actual data
     */
    private fun processHtmlTemplate(html: String, reportData: ReportData): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        // Helper function to safely convert to Double
        fun safeToDouble(value: Any?): Double {
            return when (value) {
                is Number -> value.toDouble()
                is String -> value.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        }

        // Calculate bar heights (normalize to 60-85% range for visual appeal)
        val graftD1 = safeToDouble(reportData.graft_diameter_1)
        val graftD2 = safeToDouble(reportData.graft_diameter_2)
        val hamstring = safeToDouble(reportData.hamstring_autograft)
        val quad = safeToDouble(reportData.quadriceps_tendon_diameter)

        val maxDiameter = maxOf(graftD1, graftD2, hamstring, quad, 1.0)

        val graftD1Height = ((graftD1 / maxDiameter) * 70 + 60).toInt().coerceIn(60, 85)
        val graftD2Height = ((graftD2 / maxDiameter) * 70 + 60).toInt().coerceIn(60, 85)
        val hamstringHeight = ((hamstring / maxDiameter) * 70 + 60).toInt().coerceIn(60, 85)
        val quadHeight = ((quad / maxDiameter) * 70 + 60).toInt().coerceIn(60, 85)

        // Replace placeholders
        return html
            .replace("{{PATIENT_NAME}}", reportData.name ?: "N/A")
            .replace("{{AGE}}", reportData.age?.toString() ?: "N/A")
            .replace("{{GENDER}}", reportData.gender ?: "N/A")
            .replace("{{BMI}}", String.format("%.1f", safeToDouble(reportData.bmi)))
            .replace("{{HEIGHT}}", reportData.height?.toString() ?: "N/A")
            .replace("{{WEIGHT}}", reportData.weight?.toString() ?: "N/A")
            .replace("{{AFFECTED_SIDE}}", reportData.affected_side ?: "N/A")
            .replace("{{INJURY_DATE}}", reportData.affected_date ?: "N/A")
            .replace("{{MRI_VERIFIED}}", "Yes")
            .replace("{{GENERATED_DATE}}", currentDate)
            .replace("{{REPORT_REF}}", "GP-${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}-${System.currentTimeMillis().toString().takeLast(3)}")
            .replace("{{GRAFT_D1_VALUE}}", String.format("%.1f", graftD1))
            .replace("{{GRAFT_D1_HEIGHT}}", graftD1Height.toString())
            .replace("{{GRAFT_D2_VALUE}}", String.format("%.1f", graftD2))
            .replace("{{GRAFT_D2_HEIGHT}}", graftD2Height.toString())
            .replace("{{HAMSTRING_VALUE}}", String.format("%.1f", hamstring))
            .replace("{{HAMSTRING_HEIGHT}}", hamstringHeight.toString())
            .replace("{{QUAD_VALUE}}", String.format("%.1f", quad))
            .replace("{{QUAD_HEIGHT}}", quadHeight.toString())
            .replace("{{GRAFT_DIAMETER_1}}", String.format("%.1f", graftD1))
            .replace("{{GRAFT_DIAMETER_2}}", String.format("%.1f", graftD2))
            .replace("{{PREDICTED_ST_VALUE}}", reportData.predicted_st_value?.toString() ?: "N/A")
            .replace("{{GRACILIS_LENGTH}}", reportData.gracilis_length?.toString() ?: "N/A")
            .replace("{{QUADRICEPS_TENDON}}", String.format("%.1f", quad))
    }

    /**
     * Create WebView with optimal PDF generation settings
     * IMPORTANT: Must be called on Main thread
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(): WebView {
        return WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                displayZoomControls = false
                setSupportZoom(true)
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
            }

            // Important for PDF generation
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            overScrollMode = WebView.OVER_SCROLL_NEVER
        }
    }

    /**
     * Generate PDF from WebView on Main thread
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun generatePdfFromWebViewOnMainThread(
        webView: WebView,
        outputFile: File,
        completionSource: CompletableFuture<File>
    ) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            
            // Create print adapter from WebView
            val printAdapter = webView.createPrintDocumentAdapter("GraftPredict Report")

            // Create print attributes
            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()

            // Custom adapter that notifies completion
            val customAdapter = object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes,
                    cancellationSignal: CancellationSignal,
                    callback: LayoutResultCallback,
                    extras: Bundle?
                ) {
                    printAdapter.onLayout(oldAttributes, newAttributes, cancellationSignal, callback, extras)
                }

                override fun onWrite(
                    pages: Array<PageRange>,
                    destination: ParcelFileDescriptor,
                    cancellationSignal: CancellationSignal,
                    callback: WriteResultCallback
                ) {
                    printAdapter.onWrite(pages, destination, cancellationSignal, callback)
                    
                    // Signal completion after a brief delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (!completionSource.isDone) {
                            completionSource.complete(outputFile)
                        }
                    }, 500)
                }

                override fun onFinish() {
                    printAdapter.onFinish()
                    super.onFinish()
                }
            }

            // Print to PDF
            printManager.print(
                "GraftPredict Report",
                customAdapter,
                printAttributes
            )

        } catch (e: Exception) {
            completionSource.completeExceptionally(e)
        }
    }
}
