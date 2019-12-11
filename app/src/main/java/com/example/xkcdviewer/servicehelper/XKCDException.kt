package com.example.xkcdviewer.servicehelper

/**
 * XKCD Exceptions
 */
class XKCDException(
    val errorDescription: String,
    rootCause: Throwable?
) : Exception(errorDescription, rootCause) {

    object GeneralErrors {
        /**
         * Indicates a network error occurred.
         */
        val NETWORK_ERROR = generalEx("Network error")

        /**
         * Invalid Token
         */
        val INVALID_ACCESS_TOKEN = generalEx("Invalid access token")

        /**
         * Request Timeout
         */
        val REQUEST_TIMEOUT = generalEx("Request timeout")

        /**
         * Indicates a problem occurred deserializing JSON.
         */
        val JSON_DESERIALIZATION_ERROR = generalEx("JSON deserialization error")

        /**
         * General error
         */
        val GENERIC_ERROR = generalEx("Generic error")
    }

    companion object {
        private fun generalEx(errorDescription: String): XKCDException {
            return XKCDException(errorDescription, null)
        }

        fun fromTemplate(ex: XKCDException, rootCause: Throwable?): XKCDException {
            return XKCDException(ex.errorDescription, rootCause)
        }

    }
}
