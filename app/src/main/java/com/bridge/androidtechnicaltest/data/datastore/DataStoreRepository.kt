package com.bridge.androidtechnicaltest.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.bridge.androidtechnicaltest.domain.SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        /**
         * Clear all stored preferences
         */
        suspend fun clearAllPreferences() {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        }

        /**
         * Get the pupil sync state as a Flow
         * Default value is OUT_OF_DATE
         */
        fun getPupilSyncState(): Flow<SyncState> =
            dataStore.data.map { preferences ->
                val stateString = preferences[DataStoreConstants.PUPIL_SYNC_STATE]
                try {
                    stateString?.let { SyncState.valueOf(it) } ?: SyncState.OUT_OF_DATE
                } catch (e: IllegalArgumentException) {
                    Timber.e(e, "Invalid sync state value: $stateString, defaulting to OUT_OF_DATE")
                    SyncState.OUT_OF_DATE
                }
            }

        /**
         * Set the pupil sync state
         */
        suspend fun setPupilSyncState(syncState: SyncState) {
            dataStore.edit { preferences ->
                preferences[DataStoreConstants.PUPIL_SYNC_STATE] = syncState.name
            }
        }

        /**
         * Get the current pupil sync state synchronously
         * Default value is OUT_OF_DATE
         */
        suspend fun getPupilSyncStateSync(): SyncState = getPupilSyncState().first()
    }
