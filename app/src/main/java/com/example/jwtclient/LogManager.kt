package com.example.jwtclient

import android.util.Log

object LogManager {
    private const val TAG = "JWT_CLIENT"

    fun logAction(action: String) {
        Log.d(TAG, "ACTION: $action")
    }

    fun logResponse(endpoint: String, response: String) {
        Log.i(TAG, "RESPONSE [$endpoint]: $response")
    }

    fun logError(endpoint: String, error: String) {
        Log.e(TAG, "ERROR [$endpoint]: $error")
    }
}
