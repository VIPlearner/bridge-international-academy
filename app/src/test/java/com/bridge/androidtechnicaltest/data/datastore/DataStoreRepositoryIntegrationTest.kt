package com.bridge.androidtechnicaltest.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreRepositoryIntegrationTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var dataStoreRepository: DataStoreRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        val testFile = File(temporaryFolder.newFolder(), "test_preferences.preferences_pb")
        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testFile }
        )
        dataStoreRepository = DataStoreRepository(testDataStore)
    }

    @After
    fun tearDown() {
        temporaryFolder.delete()
    }

    @Test
    fun `initial state returns false for sample toggle`() = testScope.runTest {
        val result = dataStoreRepository.getSampleToggleState().first()

        assertFalse(result)
    }

    @Test
    fun `setSampleToggleState persists true value`() = testScope.runTest {
        dataStoreRepository.setSampleToggleState(true)
        val result = dataStoreRepository.getSampleToggleState().first()

        assertTrue(result)
    }

    @Test
    fun `setSampleToggleState persists false value`() = testScope.runTest {
        dataStoreRepository.setSampleToggleState(true)
        assertTrue(dataStoreRepository.getSampleToggleState().first())

        dataStoreRepository.setSampleToggleState(false)
        val result = dataStoreRepository.getSampleToggleState().first()

        assertFalse(result)
    }

    @Test
    fun `toggleSampleState changes from false to true`() = testScope.runTest {
        assertFalse(dataStoreRepository.getSampleToggleState().first())

        dataStoreRepository.toggleSampleState()
        val result = dataStoreRepository.getSampleToggleState().first()

        assertTrue(result)
    }

    @Test
    fun `toggleSampleState changes from true to false`() = testScope.runTest {
        dataStoreRepository.setSampleToggleState(true)
        assertTrue(dataStoreRepository.getSampleToggleState().first())

        dataStoreRepository.toggleSampleState()
        val result = dataStoreRepository.getSampleToggleState().first()

        assertFalse(result)
    }

    @Test
    fun `multiple toggles work correctly`() = testScope.runTest {
        assertFalse(dataStoreRepository.getSampleToggleState().first())

        dataStoreRepository.toggleSampleState()
        assertTrue(dataStoreRepository.getSampleToggleState().first())

        dataStoreRepository.toggleSampleState()
        assertFalse(dataStoreRepository.getSampleToggleState().first())

        dataStoreRepository.toggleSampleState()
        assertTrue(dataStoreRepository.getSampleToggleState().first())
    }

    @Test
    fun `clearAllPreferences removes all stored values`() = testScope.runTest {
        dataStoreRepository.setSampleToggleState(true)
        assertTrue(dataStoreRepository.getSampleToggleState().first())

        dataStoreRepository.clearAllPreferences()
        val result = dataStoreRepository.getSampleToggleState().first()

        assertFalse(result)
    }

    @Test
    fun `persistence across repository instances`() = testScope.runTest {
        dataStoreRepository.setSampleToggleState(true)
        assertTrue(dataStoreRepository.getSampleToggleState().first())

        val newRepository = DataStoreRepository(testDataStore)
        val result = newRepository.getSampleToggleState().first()

        assertTrue(result)
    }

    @Test
    fun `flow emits updates when value changes`() = testScope.runTest {
        val flow = dataStoreRepository.getSampleToggleState()

        assertEquals(false, flow.first())

        dataStoreRepository.setSampleToggleState(true)

        assertEquals(true, flow.first())

        dataStoreRepository.toggleSampleState()

        assertEquals(false, flow.first())
    }
}
