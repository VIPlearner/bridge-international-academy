package com.bridge.androidtechnicaltest.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * Get the sample toggle value as a Flow
     */
    fun getSampleToggle(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreConstants.SAMPLE_TOGGLE_KEY] ?: false
        }
    }

    /**
     * Set the sample toggle value
     */
    suspend fun setSampleToggle(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStoreConstants.SAMPLE_TOGGLE_KEY] = enabled
        }
    }

    /**
     * Clear all preferences
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
