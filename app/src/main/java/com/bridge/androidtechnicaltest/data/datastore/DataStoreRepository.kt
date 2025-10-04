package com.bridge.androidtechnicaltest.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * Get the sample toggle state as a Flow
     */
    fun getSampleToggleState(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DataStoreConstants.SAMPLE_TOGGLE_KEY] ?: false
        }
    }

    /**
     * Toggle the sample boolean value
     */
    suspend fun toggleSampleState() {
        val currentValue = getSampleToggleState().first()
        setSampleToggleState(!currentValue)
    }

    /**
     * Set the sample toggle state explicitly
     */
    suspend fun setSampleToggleState(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStoreConstants.SAMPLE_TOGGLE_KEY] = enabled
        }
    }

    /**
     * Clear all stored preferences
     */
    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
