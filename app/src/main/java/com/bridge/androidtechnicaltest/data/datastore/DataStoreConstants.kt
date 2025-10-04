package com.bridge.androidtechnicaltest.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreConstants {
    const val PREFERENCES_DATASTORE_NAME = "app_preferences"

    val SAMPLE_TOGGLE_KEY = booleanPreferencesKey("sample_toggle")

    val PUPIL_SYNC_STATE = stringPreferencesKey("pupil_sync_state")
}
