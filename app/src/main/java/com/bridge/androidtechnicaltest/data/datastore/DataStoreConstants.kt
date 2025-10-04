package com.bridge.androidtechnicaltest.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey

object DataStoreConstants {
    const val PREFERENCES_DATASTORE_NAME = "app_preferences"

    // Sample boolean toggle key
    val SAMPLE_TOGGLE_KEY = booleanPreferencesKey("sample_toggle")
}
