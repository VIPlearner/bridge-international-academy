package com.bridge.androidtechnicaltest.data.repository

import com.bridge.androidtechnicaltest.data.db.AppDatabase
import com.bridge.androidtechnicaltest.data.db.dao.PupilDao
import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.data.mapper.toCreatePupilRequest
import com.bridge.androidtechnicaltest.data.mapper.toUpdatePupilRequest
import com.bridge.androidtechnicaltest.data.network.PupilApi
import com.bridge.androidtechnicaltest.data.network.dto.PupilResponse
import com.bridge.androidtechnicaltest.data.sync.PupilSyncManager
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class PupilRepositoryTest {

    private lateinit var repository: PupilRepository
    private lateinit var database: AppDatabase
    private lateinit var pupilDao: PupilDao
    private lateinit var pupilApi: PupilApi
    private lateinit var syncManager: PupilSyncManager

    @Before
    fun setup() {
        database = mockk()
        pupilDao = mockk()
        pupilApi = mockk()
        syncManager = mockk(relaxed = true)

        every { database.pupilDao } returns pupilDao
        every { pupilDao.pupils } returns flowOf(emptyList())

        repository = PupilRepository(database, pupilApi, syncManager)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun pupilsFlowReturnsDataFromDao() = runTest {
        val testPupils = listOf(
            createTestPupil(pupilId = 1, name = "John Doe"),
            createTestPupil(pupilId = 2, name = "Jane Smith")
        )
        every { pupilDao.pupils } returns flowOf(testPupils)

        val pupils = repository.pupils
        val result = pupils.first()

        assertEquals(testPupils, result)
    }

    @Test
    fun addPupilSuccessfullyCreatesRemotePupilAndUpdatesLocal() = runTest {
        val localPupil = createTestPupil(name = "John Doe")
        val remotePupilResponse = PupilResponse(
            pupilId = 100,
            name = "John Doe",
            country = "USA",
            image = null,
            latitude = 40.7128,
            longitude = -74.0060
        )
        val expectedUpdatedPupil = localPupil.copy(
            remoteId = 100,
            syncType = null,
            pendingSync = false
        )

        coEvery { pupilApi.createPupil(localPupil.toCreatePupilRequest()) } returns Response.success(remotePupilResponse)
        coEvery { pupilDao.upsert(expectedUpdatedPupil) } just Runs

        repository.addPupil(localPupil)

        coVerify { pupilApi.createPupil(localPupil.toCreatePupilRequest()) }
        coVerify { pupilDao.upsert(expectedUpdatedPupil) }
    }

    @Test
    fun addPupilWithValidationErrorMarksForSync() = runTest {
        val localPupil = createTestPupil(name = "John Doe")
        val expectedPupilForSync = localPupil.copy(
            syncType = SyncType.ADD,
            pendingSync = true
        )

        coEvery { pupilApi.createPupil(localPupil.toCreatePupilRequest()) } returns
            Response.error(400, "Validation error".toResponseBody())
        coEvery { pupilDao.upsert(expectedPupilForSync) } just Runs

        repository.addPupil(localPupil)

        coVerify { pupilDao.upsert(expectedPupilForSync) }
    }

    @Test
    fun addPupilWithServerErrorMarksForSync() = runTest {
        val localPupil = createTestPupil(name = "John Doe")
        val expectedPupilForSync = localPupil.copy(
            syncType = SyncType.ADD,
            pendingSync = true
        )

        coEvery { pupilApi.createPupil(localPupil.toCreatePupilRequest()) } returns
            Response.error(500, "Server error".toResponseBody())
        coEvery { pupilDao.upsert(expectedPupilForSync) } just Runs

        repository.addPupil(localPupil)

        coVerify { pupilDao.upsert(expectedPupilForSync) }
    }

    @Test
    fun addPupilWithSuccessfulResponseButNullBodyMarksForSync() = runTest {
        val localPupil = createTestPupil(name = "John Doe")
        val expectedPupilForSync = localPupil.copy(
            syncType = SyncType.ADD,
            pendingSync = true
        )

        coEvery { pupilApi.createPupil(localPupil.toCreatePupilRequest()) } returns
            Response.success(null)
        coEvery { pupilDao.upsert(expectedPupilForSync) } just Runs

        repository.addPupil(localPupil)

        coVerify { pupilDao.upsert(expectedPupilForSync) }
    }

    @Test
    fun updatePupilWithRemoteIdSuccessfullyUpdatesRemoteAndLocal() = runTest {
        val localPupil = createTestPupil(pupilId = 1, remoteId = 100, name = "Updated Name")
        val remotePupilResponse = PupilResponse(
            pupilId = 100,
            name = "Updated Name",
            country = "USA",
            image = null,
            latitude = 40.7128,
            longitude = -74.0060
        )
        val expectedUpdatedPupil = localPupil.copy(
            remoteId = 100,
            syncType = null,
            pendingSync = false
        )

        coEvery { pupilApi.updatePupil(100, localPupil.toUpdatePupilRequest()) } returns
            Response.success(remotePupilResponse)
        coEvery { pupilDao.upsert(expectedUpdatedPupil) } just Runs

        repository.updatePupil(localPupil)

        coVerify { pupilApi.updatePupil(100, localPupil.toUpdatePupilRequest()) }
        coVerify { pupilDao.upsert(expectedUpdatedPupil) }
    }

    @Test
    fun updatePupilWithRemoteIdValidationErrorMarksForSync() = runTest {
        val localPupil = createTestPupil(pupilId = 1, remoteId = 100, name = "Updated Name")
        val expectedPupilForSync = localPupil.copy(
            syncType = SyncType.UPDATE,
            pendingSync = true
        )

        coEvery { pupilApi.updatePupil(100, localPupil.toUpdatePupilRequest()) } returns
            Response.error(400, "Validation error".toResponseBody())
        coEvery { pupilDao.upsert(expectedPupilForSync) } just Runs

        repository.updatePupil(localPupil)

        coVerify { pupilDao.upsert(expectedPupilForSync) }
    }

    @Test
    fun updatePupilWithRemoteIdNotFoundFallsBackToAdd() = runTest {
        val localPupil = createTestPupil(pupilId = 1, remoteId = 100, name = "Updated Name")
        val remotePupilResponse = PupilResponse(
            pupilId = 101,
            name = "Updated Name",
            country = "USA",
            image = null,
            latitude = 40.7128,
            longitude = -74.0060
        )
        val expectedUpdatedPupil = localPupil.copy(
            remoteId = 101,
            syncType = null,
            pendingSync = false
        )

        coEvery { pupilApi.updatePupil(100, localPupil.toUpdatePupilRequest()) } returns
            Response.error(404, "Not found".toResponseBody())
        coEvery { pupilApi.createPupil(localPupil.toCreatePupilRequest()) } returns
            Response.success(remotePupilResponse)
        coEvery { pupilDao.upsert(expectedUpdatedPupil) } just Runs

        repository.updatePupil(localPupil)

        coVerify { pupilApi.updatePupil(100, localPupil.toUpdatePupilRequest()) }
        coVerify { pupilApi.createPupil(localPupil.toCreatePupilRequest()) }
        coVerify { pupilDao.upsert(expectedUpdatedPupil) }
    }

    @Test
    fun updatePupilWithRemoteIdServerErrorMarksForSync() = runTest {
        val localPupil = createTestPupil(pupilId = 1, remoteId = 100, name = "Updated Name")
        val expectedPupilForSync = localPupil.copy(
            syncType = SyncType.UPDATE,
            pendingSync = true
        )

        coEvery { pupilApi.updatePupil(100, localPupil.toUpdatePupilRequest()) } returns
            Response.error(500, "Server error".toResponseBody())
        coEvery { pupilDao.upsert(expectedPupilForSync) } just Runs

        repository.updatePupil(localPupil)

        coVerify { pupilDao.upsert(expectedPupilForSync) }
    }

    @Test
    fun updatePupilWithoutRemoteIdFallsBackToAdd() = runTest {
        val localPupil = createTestPupil(pupilId = 1, remoteId = null, name = "New Name")
        val remotePupilResponse = PupilResponse(
            pupilId = 101,
            name = "New Name",
            country = "USA",
            image = null,
            latitude = 40.7128,
            longitude = -74.0060
        )
        val expectedUpdatedPupil = localPupil.copy(
            remoteId = 101,
            syncType = null,
            pendingSync = false
        )

        coEvery { pupilApi.createPupil(localPupil.toCreatePupilRequest()) } returns
            Response.success(remotePupilResponse)
        coEvery { pupilDao.upsert(expectedUpdatedPupil) } just Runs

        repository.updatePupil(localPupil)

        coVerify(exactly = 0) { pupilApi.updatePupil(any(), any()) }
        coVerify { pupilApi.createPupil(localPupil.toCreatePupilRequest()) }
        coVerify { pupilDao.upsert(expectedUpdatedPupil) }
    }

    @Test
    fun deletePupilWithRemoteIdSuccessfullyDeletesRemoteAndLocal() = runTest {
        val localPupil = createTestPupil(pupilId = 1, remoteId = 100)

        coEvery { pupilDao.getPupilById(1) } returns localPupil
        coEvery { pupilApi.deletePupil(100) } returns Response.success(Unit)
        coEvery { pupilDao.deletePupilById(1) } returns 1

        repository.deletePupil(1)

        coVerify { pupilApi.deletePupil(100) }
        coVerify { pupilDao.deletePupilById(1) }
    }

    @Test
    fun deletePupilWithRemoteIdNotFoundDeletesLocal() = runTest {
        val localPupil = createTestPupil(pupilId = 1, remoteId = 100)

        coEvery { pupilDao.getPupilById(1) } returns localPupil
        coEvery { pupilApi.deletePupil(100) } returns Response.error(404, "Not found".toResponseBody())
        coEvery { pupilDao.deletePupilById(1) } returns 1

        repository.deletePupil(1)

        coVerify { pupilApi.deletePupil(100) }
        coVerify { pupilDao.deletePupilById(1) }
    }

    @Test
    fun deletePupilWithRemoteIdServerErrorMarksForSync() = runTest {
        val localPupil = createTestPupil(pupilId = 1, remoteId = 100)

        coEvery { pupilDao.getPupilById(1) } returns localPupil
        coEvery { pupilApi.deletePupil(100) } returns Response.error(500, "Server error".toResponseBody())
        coEvery { pupilDao.markForSync(1, SyncType.DELETE) } returns 1

        repository.deletePupil(1)

        coVerify { pupilApi.deletePupil(100) }
        coVerify { pupilDao.markForSync(1, SyncType.DELETE) }
        coVerify(exactly = 0) { pupilDao.deletePupilById(1) }
    }

    @Test
    fun deletePupilWithoutRemoteIdDeletesLocalOnly() = runTest {
        val localPupil = createTestPupil(pupilId = 1, remoteId = null)

        coEvery { pupilDao.getPupilById(1) } returns localPupil
        coEvery { pupilDao.deletePupilById(1) } returns 1

        repository.deletePupil(1)

        coVerify(exactly = 0) { pupilApi.deletePupil(any()) }
        coVerify { pupilDao.deletePupilById(1) }
    }

    @Test
    fun deletePupilNotFoundInDatabaseDoesNothing() = runTest {
        coEvery { pupilDao.getPupilById(1) } returns null

        repository.deletePupil(1)

        coVerify(exactly = 0) { pupilApi.deletePupil(any()) }
        coVerify(exactly = 0) { pupilDao.deletePupilById(any()) }
        coVerify(exactly = 0) { pupilDao.markForSync(any(), any()) }
    }

    @Test
    fun startSyncCallsSyncManager() {
        repository.startSync()

        verify { syncManager.startPeriodicSync() }
    }

    @Test
    fun stopSyncCallsSyncManager() {
        repository.stopSync()

        verify { syncManager.stopPeriodicSync() }
    }

    private fun createTestPupil(
        pupilId: Int = 1,
        remoteId: Int? = null,
        name: String = "Test Pupil",
        country: String = "Test Country",
        image: String? = null,
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        pendingSync: Boolean = false,
        syncType: SyncType? = null
    ) = Pupil(
        pupilId = pupilId,
        remoteId = remoteId,
        name = name,
        country = country,
        image = image,
        latitude = latitude,
        longitude = longitude,
        pendingSync = pendingSync,
        syncType = syncType
    )
}
