package com.simats.graftpredict.ml

import android.content.Context
import android.net.Uri
import android.util.Log

/**
 * Kotlin wrapper for the ACLModelPredictor Java class.
 * Provides a simpler interface for Jetpack Compose integration.
 */
class ACLPredictorWrapper(private val context: Context) {
    private val predictor = ACLModelPredictor(context)

    suspend fun predictFromImages(
        imageUris: List<Uri>,
        onProgress: (String) -> Unit
    ): String {
        return try {
            onProgress("=== Starting ACL MRI Prediction ===")
            onProgress("$ Preprocessing selected MRI images...")
            
            // Run the prediction (blocking operation, should be on IO thread)
            val result = predictor.predictFromImages(imageUris, context)
            
            Log.i("ACLPredictorWrapper", "Prediction completed: $result")
            result ?: ""
        } catch (e: Exception) {
            Log.e("ACLPredictorWrapper", "Prediction failed", e)
            onProgress("\n$ ERROR: ${e.message ?: "Unknown error occurred"}")
            "\n$ ERROR: ${e.message ?: "Prediction failed"}"
        }
    }

    fun close() {
        try {
            predictor.close()
        } catch (e: Exception) {
            Log.e("ACLPredictorWrapper", "Error closing predictor", e)
        }
    }
}
