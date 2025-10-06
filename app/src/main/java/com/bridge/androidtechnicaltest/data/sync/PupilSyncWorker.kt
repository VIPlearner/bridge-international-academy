package com.bridge.androidtechnicaltest.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class PupilSyncWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParams: WorkerParameters,
        private val syncService: PupilSyncService,
    ) : CoroutineWorker(context, workerParams) {
        override suspend fun doWork(): Result =
            withContext(Dispatchers.IO) {
                try {
                    Timber.d("PupilSyncWorker started")
                    val success = syncService.sync()
                    if (success) {
                        Result.success()
                    } else {
                        Result.retry()
                    }
                } catch (e: Exception) {
                    Timber.e(e, "PupilSyncWorker failed with exception")
                    Result.retry()
                }
            }
    }
