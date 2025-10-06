package com.bridge.androidtechnicaltest.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import com.bridge.androidtechnicaltest.domain.SyncState
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DataStoreRepositoryTest {
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataStoreRepository: DataStoreRepository

    @Before
    fun setup() {
        dataStore = mockk()
        dataStoreRepository = DataStoreRepository(dataStore)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getPupilSyncState returns OUT_OF_DATE when preference is not set`() =
        runTest {
            val emptyPreferences = preferencesOf()
            every { dataStore.data } returns flowOf(emptyPreferences)

            val result = dataStoreRepository.getPupilSyncState().first()

            assertEquals(SyncState.OUT_OF_DATE, result)
        }

    @Test
    fun `getPupilSyncState returns OUT_OF_DATE when preference has invalid value`() =
        runTest {
            val preferences = preferencesOf(DataStoreConstants.PUPIL_SYNC_STATE to "INVALID_STATE")
            every { dataStore.data } returns flowOf(preferences)

            val result = dataStoreRepository.getPupilSyncState().first()

            assertEquals(SyncState.OUT_OF_DATE, result)
        }

    @Test
    fun `getPupilSyncStateSync returns current sync state`() =
        runTest {
            val preferences = preferencesOf(DataStoreConstants.PUPIL_SYNC_STATE to "SYNCING")
            every { dataStore.data } returns flowOf(preferences)

            val result = dataStoreRepository.getPupilSyncStateSync()

            assertEquals(SyncState.SYNCING, result)
        }
}
