package com.bridge.androidtechnicaltest.config

import com.bridge.androidtechnicaltest.BuildConfig

/**
 * Central location for API configuration constants.
 * This class provides secure access to API keys through BuildConfig.
 */
object ApiConstants {
    /**
     * OpenWeatherMap Geocoding API key.
     * This key is read from local.properties at build time and exposed through BuildConfig.
     * The actual key value is not stored in source code for security.
     */
    const val GEOCODING_API_KEY: String = BuildConfig.GEOCODING_API_KEY

    const val PUPIL_API_REQUEST_ID = BuildConfig.PUPIL_API_REQUEST_ID
}
