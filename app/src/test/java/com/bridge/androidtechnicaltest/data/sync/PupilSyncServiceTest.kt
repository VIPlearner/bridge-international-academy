package com.bridge.androidtechnicaltest.data.sync

import com.bridge.androidtechnicaltest.data.datastore.DataStoreRepository
import com.bridge.androidtechnicaltest.data.db.dao.PupilDao
import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.data.network.PupilApi
import com.bridge.androidtechnicaltest.data.network.dto.PupilPageResponse
import com.bridge.androidtechnicaltest.data.network.dto.PupilResponse
import com.bridge.androidtechnicaltest.domain.SyncState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class PupilSyncServiceTest {
    private lateinit var pupilDao: PupilDao
    private lateinit var pupilApi: PupilApi
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var pupilSyncService: PupilSyncService

    @Before
    fun setup() {
        pupilDao = mockk()
        pupilApi = mockk()
        dataStoreRepository = mockk()
        pupilSyncService = PupilSyncService(pupilDao, pupilApi, dataStoreRepository)

        coEvery { dataStoreRepository.setPupilSyncState(any()) } just Runs
        coEvery { dataStoreRepository.getPupilSyncState() } returns flowOf(SyncState.UP_TO_DATE)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun sync_whenAlreadySyncing_returnsFalse() =
        runBlocking {
            coEvery { dataStoreRepository.getPupilSyncState() } returns flowOf(SyncState.SYNCING)

            val result = pupilSyncService.sync()

            assertFalse(result)
            coVerify(exactly = 0) { pupilDao.getPendingSyncPupils() }
        }

    @Test
    fun sync_noPendingSync_returnsTrue() =
        runBlocking {
            coEvery { pupilDao.getPendingSyncPupils() } returns emptyList()
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(
                        items = emptyList(),
                        pageNumber = 1,
                        itemCount = 0,
                        totalPages = 1,
                    ),
                )

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.SYNCING) }
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.UP_TO_DATE) }
        }

    @Test
    fun sync_addPupilSuccess_returnsTrue() =
        runBlocking {
            val pendingAddPupil =
                createTestPupil(
                    pupilId = 1,
                    name = "John Doe",
                    pendingSync = true,
                    syncType = SyncType.ADD,
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingAddPupil)
            coEvery { pupilApi.createPupil(any()) } returns
                Response.success(
                    PupilResponse(
                        pupilId = 100,
                        name = "John Doe",
                        country = "USA",
                        image = null,
                        latitude = 40.7128,
                        longitude = -74.0060,
                    ),
                )
            coEvery { pupilDao.upsert(any()) } just Runs
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1),
                )

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify { pupilApi.createPupil(any()) }
            coVerify { pupilDao.upsert(match { !it.pendingSync && it.syncType == null && it.remoteId == 100 }) }
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.UP_TO_DATE) }
        }

    @Test
    fun sync_updatePupilSuccess_returnsTrue() =
        runBlocking {
            val pendingUpdatePupil =
                createTestPupil(
                    pupilId = 1,
                    remoteId = 100,
                    name = "Jane Doe Updated",
                    pendingSync = true,
                    syncType = SyncType.UPDATE,
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingUpdatePupil)
            coEvery { pupilApi.updatePupil(100, any()) } returns
                Response.success(
                    PupilResponse(
                        pupilId = 100,
                        name = "Jane Doe Updated",
                        country = "USA",
                        image = null,
                        latitude = 40.7128,
                        longitude = -74.0060,
                    ),
                )
            coEvery { pupilDao.upsert(any()) } just Runs
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1),
                )

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify { pupilApi.updatePupil(100, any()) }
            coVerify { pupilDao.upsert(match { !it.pendingSync && it.syncType == null }) }
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.UP_TO_DATE) }
        }

    @Test
    fun sync_updatePupilWithoutRemoteId_fallsBackToAdd() =
        runBlocking {
            val pendingUpdatePupil =
                createTestPupil(
                    pupilId = 1,
                    remoteId = null,
                    name = "Jane Doe",
                    pendingSync = true,
                    syncType = SyncType.UPDATE,
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingUpdatePupil)
            coEvery { pupilApi.createPupil(any()) } returns
                Response.success(
                    PupilResponse(
                        pupilId = 101,
                        name = "Jane Doe",
                        country = "Canada",
                        image = null,
                        latitude = 45.4215,
                        longitude = -75.6972,
                    ),
                )
            coEvery { pupilDao.upsert(any()) } just Runs
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1),
                )

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify { pupilApi.createPupil(any()) }
            coVerify(exactly = 0) { pupilApi.updatePupil(any(), any()) }
        }

    @Test
    fun sync_deletePupilSuccess_returnsTrue() =
        runBlocking {
            val pendingDeletePupil =
                createTestPupil(
                    pupilId = 1,
                    remoteId = 100,
                    name = "To Be Deleted",
                    pendingSync = true,
                    syncType = SyncType.DELETE,
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingDeletePupil)
            coEvery { pupilApi.deletePupil(100) } returns Response.success(Unit)
            coEvery { pupilDao.delete(any()) } returns 1
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1),
                )

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify { pupilApi.deletePupil(100) }
            coVerify { pupilDao.delete(pendingDeletePupil) }
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.UP_TO_DATE) }
        }

    @Test
    fun sync_deletePupilWithoutRemoteId_skipsDelete() =
        runBlocking {
            val pendingDeletePupil =
                createTestPupil(
                    pupilId = 1,
                    remoteId = null,
                    name = "Local Only Pupil",
                    pendingSync = true,
                    syncType = SyncType.DELETE,
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingDeletePupil)
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1),
                )

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify(exactly = 0) { pupilApi.deletePupil(any()) }
            coVerify(exactly = 0) { pupilDao.delete(any()) }
        }

    @Test
    fun sync_mixedSyncTypes_returnsTrue() =
        runBlocking {
            val pendingPupils =
                listOf(
                    createTestPupil(pupilId = 1, name = "Add Me", pendingSync = true, syncType = SyncType.ADD),
                    createTestPupil(pupilId = 2, remoteId = 200, name = "Update Me", pendingSync = true, syncType = SyncType.UPDATE),
                    createTestPupil(pupilId = 3, remoteId = 300, name = "Delete Me", pendingSync = true, syncType = SyncType.DELETE),
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns pendingPupils
            coEvery { pupilApi.createPupil(any()) } returns
                Response.success(
                    PupilResponse(pupilId = 102, name = "Add Me", country = "UK", image = null, latitude = 51.5074, longitude = -0.1278),
                )
            coEvery { pupilApi.updatePupil(200, any()) } returns
                Response.success(
                    PupilResponse(
                        pupilId = 200,
                        name = "Update Me",
                        country = "France",
                        image = null,
                        latitude = 48.8566,
                        longitude = 2.3522,
                    ),
                )
            coEvery { pupilApi.deletePupil(300) } returns Response.success(Unit)
            coEvery { pupilDao.upsert(any()) } just Runs
            coEvery { pupilDao.delete(any()) } returns 1
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1),
                )

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify { pupilApi.createPupil(any()) }
            coVerify { pupilApi.updatePupil(200, any()) }
            coVerify { pupilApi.deletePupil(300) }
        }

    @Test
    fun sync_syncFailure_returnsFalse() =
        runBlocking {
            val pendingAddPupil =
                createTestPupil(
                    pupilId = 1,
                    name = "Failed Add",
                    pendingSync = true,
                    syncType = SyncType.ADD,
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingAddPupil)
            coEvery { pupilApi.createPupil(any()) } returns Response.error(500, mockk(relaxed = true))

            val result = pupilSyncService.sync()

            assertFalse(result)
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE) }
        }

    @Test
    fun sync_apiException_returnsFalse() =
        runBlocking {
            val pendingAddPupil =
                createTestPupil(
                    pupilId = 1,
                    name = "Exception Test",
                    pendingSync = true,
                    syncType = SyncType.ADD,
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingAddPupil)
            coEvery { pupilApi.createPupil(any()) } throws RuntimeException("Network error")

            val result = pupilSyncService.sync()

            assertFalse(result)
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE) }
        }

    @Test
    fun sync_fetchPupilsWithPagination_returnsTrue() =
        runBlocking {
            val pupilsPage1 =
                listOf(
                    PupilResponse(1, "Pupil 1", "USA", null, 40.7128, -74.0060),
                    PupilResponse(2, "Pupil 2", "Canada", null, 45.4215, -75.6972),
                )
            val pupilsPage2 =
                listOf(
                    PupilResponse(3, "Pupil 3", "UK", null, 51.5074, -0.1278),
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns emptyList()
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(items = pupilsPage1, pageNumber = 1, itemCount = 3, totalPages = 2),
                )
            coEvery { pupilApi.getPupils(page = 2) } returns
                Response.success(
                    PupilPageResponse(items = pupilsPage2, pageNumber = 2, itemCount = 3, totalPages = 2),
                )
            coEvery { pupilDao.updatePupilWithRemoteInfo(any(), any(), any(), any(), any(), any()) } returns 1

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify { pupilApi.getPupils(page = 1) }
            coVerify { pupilApi.getPupils(page = 2) }
            coVerify(exactly = 3) { pupilDao.updatePupilWithRemoteInfo(any(), any(), any(), any(), any(), any()) }
        }

    @Test
    fun sync_fetchPupilsFailure_returnsFalse() =
        runBlocking {
            coEvery { pupilDao.getPendingSyncPupils() } returns emptyList()
            coEvery { pupilApi.getPupils(page = 1) } returns Response.error(500, mockk(relaxed = true))

            val result = pupilSyncService.sync()

            assertFalse(result)
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE) }
        }

    @Test
    fun sync_fetchPupilsPaginationFailure_returnsFalse() =
        runBlocking {
            val pupilsPage1 =
                listOf(
                    PupilResponse(1, "Pupil 1", "USA", null, 40.7128, -74.0060),
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns emptyList()
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(items = pupilsPage1, pageNumber = 1, itemCount = 2, totalPages = 2),
                )
            coEvery { pupilApi.getPupils(page = 2) } returns Response.error(500, mockk(relaxed = true))
            coEvery { pupilDao.updatePupilWithRemoteInfo(any(), any(), any(), any(), any(), any()) } returns 1

            val result = pupilSyncService.sync()

            assertFalse(result)
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE) }
        }

    @Test
    fun sync_pupilWithoutPendingSync_skipsSync() =
        runBlocking {
            val nonPendingPupil =
                createTestPupil(
                    pupilId = 1,
                    name = "No Sync Needed",
                    pendingSync = false,
                    syncType = null,
                )

            coEvery { pupilDao.getPendingSyncPupils() } returns listOf(nonPendingPupil)
            coEvery { pupilApi.getPupils(page = 1) } returns
                Response.success(
                    PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1),
                )

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify(exactly = 0) { pupilApi.createPupil(any()) }
            coVerify(exactly = 0) { pupilApi.updatePupil(any(), any()) }
            coVerify(exactly = 0) { pupilApi.deletePupil(any()) }
        }

    @Test
    fun sync_fetch404Response_returnsTrue() =
        runBlocking {
            coEvery { pupilDao.getPendingSyncPupils() } returns emptyList()
            coEvery { pupilApi.getPupils(page = 1) } returns Response.error(404, mockk(relaxed = true))

            val result = pupilSyncService.sync()

            assertTrue(result)
            coVerify { dataStoreRepository.setPupilSyncState(SyncState.UP_TO_DATE) }
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
        syncType: SyncType? = null,
    ) = Pupil(
        pupilId = pupilId,
        remoteId = remoteId,
        name = name,
        country = country,
        image = image,
        latitude = latitude,
        longitude = longitude,
        pendingSync = pendingSync,
        syncType = syncType,
    )
}
