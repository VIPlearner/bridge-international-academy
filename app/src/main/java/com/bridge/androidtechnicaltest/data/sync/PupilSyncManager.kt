package com.bridge.androidtechnicaltest.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.Operation
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PupilSyncManager
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        private val workManager = WorkManager.getInstance(context)

        companion object {
            private const val SYNC_WORK_NAME = "pupil_sync_work"
            private const val SYNC_INTERVAL_MINUTES = 30L
        }

        fun startPeriodicSync(): WorkInfo? {
            val constraints =
                Constraints
                    .Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val syncWorkRequest =
                PeriodicWorkRequestBuilder<PupilSyncWorker>(
                    SYNC_INTERVAL_MINUTES,
                    TimeUnit.MINUTES,
                ).setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.SECONDS)
                    .build()

            workManager.enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest,
            )

            return workManager.getWorkInfoById(syncWorkRequest.id).get()
        }

        fun stopPeriodicSync(): Operation = workManager.cancelUniqueWork(SYNC_WORK_NAME)
    }
