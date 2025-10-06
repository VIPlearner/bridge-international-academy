package com.bridge.androidtechnicaltest.data.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PupilSyncManagerTest {
    private lateinit var context: Context

    private lateinit var pupilSyncManager: PupilSyncManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        val config =
            Configuration
                .Builder()
                .setExecutor(SynchronousExecutor())
                .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        pupilSyncManager = PupilSyncManager(context)
    }

    @Test
    fun testStartSync() {
        val workInfo = pupilSyncManager.startPeriodicSync()

        assertThat(workInfo?.state, `is`(WorkInfo.State.ENQUEUED))
    }

    @Test
    fun testStopSync() {
        val operation = pupilSyncManager.stopPeriodicSync()
        val result = operation.result.get()

        assertThat(result is Operation.State.SUCCESS, `is`(true))
    }
}
