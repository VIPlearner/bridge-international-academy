package com.bridge.androidtechnicaltest.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    fun `getSampleToggleState returns false when preference is not set`() = runTest {
        val emptyPreferences = preferencesOf()
        every { dataStore.data } returns flowOf(emptyPreferences)

        val result = dataStoreRepository.getSampleToggleState().first()

        assertFalse(result)
    }

    @Test
    fun `getSampleToggleState returns true when preference is set to true`() = runTest {
        val preferences = preferencesOf(DataStoreConstants.SAMPLE_TOGGLE_KEY to true)
        every { dataStore.data } returns flowOf(preferences)

        val result = dataStoreRepository.getSampleToggleState().first()

        assertTrue(result)
    }

    @Test
    fun `getSampleToggleState returns false when preference is set to false`() = runTest {
        val preferences = preferencesOf(DataStoreConstants.SAMPLE_TOGGLE_KEY to false)
        every { dataStore.data } returns flowOf(preferences)

        val result = dataStoreRepository.getSampleToggleState().first()

        assertFalse(result)
    }
}
