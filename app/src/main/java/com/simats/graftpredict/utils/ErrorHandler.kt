package com.simats.graftpredict.utils

/**
 * Sanitizes error messages to hide sensitive information like IP addresses and ports
 * from being displayed to users
 */
object ErrorHandler {
    /**
     * Sanitizes exception messages to remove sensitive backend information
     * @param exception The exception to sanitize
     * @param defaultMessage The default message to show if unable to sanitize
     * @return A user-friendly error message
     */
    fun sanitizeError(exception: Exception?, defaultMessage: String): String {
        if (exception == null || exception.message.isNullOrEmpty()) {
            return defaultMessage
        }

        val message = exception.message ?: return defaultMessage
        
        // Check if this is a network error containing IP/port information
        if (isNetworkError(message)) {
            return "Unable to connect to the server. Please check your internet connection and try again."
        }

        // For other exceptions, return the default message
        return defaultMessage
    }

    /**
     * Checks if an error message contains sensitive network information
     */
    private fun isNetworkError(message: String): Boolean {
        // Check for common patterns in connection errors
        return message.contains(Regex("""\d+\.\d+\.\d+\.\d+""")) || // IP address
                message.contains(Regex(""":\d{4,5}""")) || // Port number
                message.contains("Failed to connect", ignoreCase = true) ||
                message.contains("Connection refused", ignoreCase = true) ||
                message.contains("Connection timeout", ignoreCase = true) ||
                message.contains("Unknown host", ignoreCase = true) ||
                message.contains("Network unreachable", ignoreCase = true)
    }
}
