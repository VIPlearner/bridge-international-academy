package com.bridge.androidtechnicaltest.data.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    /**
     * Get the sample toggle state
     */
    fun getSampleToggleState(): Flow<Boolean> {
        return preferencesManager.getSampleToggle()
    }

    /**
     * Toggle the sample boolean value
     */
    suspend fun toggleSampleState() {
        val currentValue = preferencesManager.getSampleToggle().first()
        preferencesManager.setSampleToggle(!currentValue)
    }

    /**
     * Set the sample toggle state explicitly
     */
    suspend fun setSampleToggleState(enabled: Boolean) {
        preferencesManager.setSampleToggle(enabled)
    }

    /**
     * Clear all stored preferences
     */
    suspend fun clearAllPreferences() {
        preferencesManager.clearAll()
    }
}
