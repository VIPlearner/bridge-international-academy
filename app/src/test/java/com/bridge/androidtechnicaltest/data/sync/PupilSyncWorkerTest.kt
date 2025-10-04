package com.bridge.androidtechnicaltest.data.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.bridge.androidtechnicaltest.data.db.AppDatabase
import com.bridge.androidtechnicaltest.data.db.dao.PupilDao
import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.data.network.PupilApi
import com.bridge.androidtechnicaltest.data.network.dto.PupilPageResponse
import com.bridge.androidtechnicaltest.data.network.dto.PupilResponse
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class PupilSyncWorkerTest {

    private lateinit var context: Context

    private lateinit var appDatabase: AppDatabase
    private lateinit var pupilApi: PupilApi
    private lateinit var pupilDao: PupilDao

    private lateinit var testWorkerFactory: TestWorkerFactory

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        appDatabase = mockk()
        pupilApi = mockk()
        pupilDao = mockk()
        every { appDatabase.pupilDao } returns pupilDao
        testWorkerFactory = TestWorkerFactory(pupilApi, appDatabase)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testPupilSyncWorker_noPendingSync() = runBlocking {
        coEvery { pupilDao.getPendingSyncPupils() } returns emptyList()
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(
                items = emptyList(),
                pageNumber = 1,
                itemCount = 5,
                totalPages = 1
            ))

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun testPupilSyncWorker_addPupilSuccess() = runBlocking {
        val pendingAddPupil = createTestPupil(
            pupilId = 1,
            name = "John Doe",
            pendingSync = true,
            syncType = SyncType.ADD
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingAddPupil)
        coEvery { pupilApi.createPupil(any()) } returns Response.success(
            PupilResponse(
                pupilId = 100,
                name = "John Doe",
                country = "USA",
                image = null,
                latitude = 40.7128,
                longitude = -74.0060
            )
        )
        coEvery { pupilDao.upsert(any()) } just Runs
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1)
        )

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { pupilApi.createPupil(any()) }
        coVerify { pupilDao.upsert(match { !it.pendingSync && it.syncType == null && it.remoteId == 100 }) }
    }

    @Test
    fun testPupilSyncWorker_updatePupilSuccess() = runBlocking {
        val pendingUpdatePupil = createTestPupil(
            pupilId = 1,
            remoteId = 100,
            name = "Jane Doe Updated",
            pendingSync = true,
            syncType = SyncType.UPDATE
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingUpdatePupil)
        coEvery { pupilApi.updatePupil(100, any()) } returns Response.success(
            PupilResponse(
                pupilId = 100,
                name = "Jane Doe Updated",
                country = "USA",
                image = null,
                latitude = 40.7128,
                longitude = -74.0060
            )
        )
        coEvery { pupilDao.upsert(any()) } just Runs
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1)
        )

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { pupilApi.updatePupil(100, any()) }
        coVerify { pupilDao.upsert(match { !it.pendingSync && it.syncType == null }) }
    }

    @Test
    fun testPupilSyncWorker_updatePupilWithoutRemoteId_fallsBackToAdd() = runBlocking {
        val pendingUpdatePupil = createTestPupil(
            pupilId = 1,
            remoteId = null, // No remote ID, should fall back to ADD
            name = "Jane Doe",
            pendingSync = true,
            syncType = SyncType.UPDATE
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingUpdatePupil)
        coEvery { pupilApi.createPupil(any()) } returns Response.success(
            PupilResponse(
                pupilId = 101,
                name = "Jane Doe",
                country = "Canada",
                image = null,
                latitude = 45.4215,
                longitude = -75.6972
            )
        )
        coEvery { pupilDao.upsert(any()) } just Runs
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1)
        )

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { pupilApi.createPupil(any()) }
        coVerify(exactly = 0) { pupilApi.updatePupil(any(), any()) }
    }

    @Test
    fun testPupilSyncWorker_deletePupilSuccess() = runBlocking {
        val pendingDeletePupil = createTestPupil(
            pupilId = 1,
            remoteId = 100,
            name = "To Be Deleted",
            pendingSync = true,
            syncType = SyncType.DELETE
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingDeletePupil)
        coEvery { pupilApi.deletePupil(100) } returns Response.success(Unit)
        coEvery { pupilDao.delete(any()) } returns 1
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1)
        )

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { pupilApi.deletePupil(100) }
        coVerify { pupilDao.delete(pendingDeletePupil) }
    }

    @Test
    fun testPupilSyncWorker_deletePupilWithoutRemoteId_skipsDelete() = runBlocking {
        val pendingDeletePupil = createTestPupil(
            pupilId = 1,
            remoteId = null, // No remote ID, should skip delete
            name = "Local Only Pupil",
            pendingSync = true,
            syncType = SyncType.DELETE
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingDeletePupil)
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1)
        )

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 0) { pupilApi.deletePupil(any()) }
        coVerify(exactly = 0) { pupilDao.delete(any()) }
    }

    @Test
    fun testPupilSyncWorker_mixedSyncTypes() = runBlocking {
        val pendingPupils = listOf(
            createTestPupil(pupilId = 1, name = "Add Me", pendingSync = true, syncType = SyncType.ADD),
            createTestPupil(pupilId = 2, remoteId = 200, name = "Update Me", pendingSync = true, syncType = SyncType.UPDATE),
            createTestPupil(pupilId = 3, remoteId = 300, name = "Delete Me", pendingSync = true, syncType = SyncType.DELETE)
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns pendingPupils
        coEvery { pupilApi.createPupil(any()) } returns Response.success(
            PupilResponse(pupilId = 102, name = "Add Me", country = "UK", image = null, latitude = 51.5074, longitude = -0.1278)
        )
        coEvery { pupilApi.updatePupil(200, any()) } returns Response.success(
            PupilResponse(pupilId = 200, name = "Update Me", country = "France", image = null, latitude = 48.8566, longitude = 2.3522)
        )
        coEvery { pupilApi.deletePupil(300) } returns Response.success(Unit)
        coEvery { pupilDao.upsert(any()) } just Runs
        coEvery { pupilDao.delete(any()) } returns 1
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1)
        )

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { pupilApi.createPupil(any()) }
        coVerify { pupilApi.updatePupil(200, any()) }
        coVerify { pupilApi.deletePupil(300) }
    }

    @Test
    fun testPupilSyncWorker_syncFailure_returnsRetry() = runBlocking {
        val pendingAddPupil = createTestPupil(
            pupilId = 1,
            name = "Failed Add",
            pendingSync = true,
            syncType = SyncType.ADD
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingAddPupil)
        coEvery { pupilApi.createPupil(any()) } returns Response.error(500, mockk(relaxed = true))

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun testPupilSyncWorker_apiException_returnsRetry() = runBlocking {
        val pendingAddPupil = createTestPupil(
            pupilId = 1,
            name = "Exception Test",
            pendingSync = true,
            syncType = SyncType.ADD
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns listOf(pendingAddPupil)
        coEvery { pupilApi.createPupil(any()) } throws RuntimeException("Network error")

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun testPupilSyncWorker_fetchPupilsWithPagination() = runBlocking {
        val pupilsPage1 = listOf(
            PupilResponse(1, "Pupil 1", "USA", null, 40.7128, -74.0060),
            PupilResponse(2, "Pupil 2", "Canada", null, 45.4215, -75.6972)
        )
        val pupilsPage2 = listOf(
            PupilResponse(3, "Pupil 3", "UK", null, 51.5074, -0.1278)
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns emptyList()
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(items = pupilsPage1, pageNumber = 1, itemCount = 3, totalPages = 2)
        )
        coEvery { pupilApi.getPupils(page = 2) } returns Response.success(
            PupilPageResponse(items = pupilsPage2, pageNumber = 2, itemCount = 3, totalPages = 2)
        )
        coEvery { pupilDao.updatePupilWithRemoteInfo(any(), any(), any(), any(), any(), any()) } returns 1

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { pupilApi.getPupils(page = 1) }
        coVerify { pupilApi.getPupils(page = 2) }
        coVerify(exactly = 3) { pupilDao.updatePupilWithRemoteInfo(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun testPupilSyncWorker_fetchPupilsFailure_returnsRetry() = runBlocking {
        coEvery { pupilDao.getPendingSyncPupils() } returns emptyList()
        coEvery { pupilApi.getPupils(page = 1) } returns Response.error(500, mockk(relaxed = true))

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun testPupilSyncWorker_fetchPupilsPaginationFailure_returnsRetry() = runBlocking {
        val pupilsPage1 = listOf(
            PupilResponse(1, "Pupil 1", "USA", null, 40.7128, -74.0060)
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns emptyList()
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(items = pupilsPage1, pageNumber = 1, itemCount = 2, totalPages = 2)
        )
        coEvery { pupilApi.getPupils(page = 2) } returns Response.error(500, mockk(relaxed = true))
        coEvery { pupilDao.updatePupilWithRemoteInfo(any(), any(), any(), any(), any(), any()) } returns 1

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun testPupilSyncWorker_pupilWithoutPendingSync_skipsSync() = runBlocking {
        val nonPendingPupil = createTestPupil(
            pupilId = 1,
            name = "No Sync Needed",
            pendingSync = false,
            syncType = null
        )

        coEvery { pupilDao.getPendingSyncPupils() } returns listOf(nonPendingPupil)
        coEvery { pupilApi.getPupils(page = 1) } returns Response.success(
            PupilPageResponse(items = emptyList(), pageNumber = 1, itemCount = 0, totalPages = 1)
        )

        val worker = TestListenableWorkerBuilder<PupilSyncWorker>(context)
            .setWorkerFactory(testWorkerFactory)
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 0) { pupilApi.createPupil(any()) }
        coVerify(exactly = 0) { pupilApi.updatePupil(any(), any()) }
        coVerify(exactly = 0) { pupilApi.deletePupil(any()) }
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