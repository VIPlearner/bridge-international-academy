package com.bridge.androidtechnicaltest.data.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PupilSyncWorkerTest {
    private lateinit var context: Context
    private lateinit var syncService: PupilSyncService
    private lateinit var testWorkerFactory: TestWorkerFactory

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        syncService = mockk()
        testWorkerFactory = TestWorkerFactory(syncService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun doWork_syncSuccess_returnsSuccess() =
        runBlocking {
            coEvery { syncService.sync() } returns true

            val worker =
                TestListenableWorkerBuilder<PupilSyncWorker>(context)
                    .setWorkerFactory(testWorkerFactory)
                    .build()

            val result = worker.doWork()

            assertEquals(ListenableWorker.Result.success(), result)
            coVerify { syncService.sync() }
        }

    @Test
    fun doWork_syncFailure_returnsRetry() =
        runBlocking {
            coEvery { syncService.sync() } returns false

            val worker =
                TestListenableWorkerBuilder<PupilSyncWorker>(context)
                    .setWorkerFactory(testWorkerFactory)
                    .build()

            val result = worker.doWork()

            assertEquals(ListenableWorker.Result.retry(), result)
            coVerify { syncService.sync() }
        }

    @Test
    fun doWork_syncException_returnsRetry() =
        runBlocking {
            coEvery { syncService.sync() } throws RuntimeException("Network error")

            val worker =
                TestListenableWorkerBuilder<PupilSyncWorker>(context)
                    .setWorkerFactory(testWorkerFactory)
                    .build()

            val result = worker.doWork()

            assertEquals(ListenableWorker.Result.retry(), result)
            coVerify { syncService.sync() }
        }
}
