package com.bridge.androidtechnicaltest.utils

import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * Custom network logger that uses Timber for HTTP request/response logging
 */
class NetworkLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        Timber.d(message)
    }
}
