package com.bridge.androidtechnicaltest.data.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class TestWorkerFactory(
    private val syncService: PupilSyncService,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? =
        when (workerClassName) {
            PupilSyncWorker::class.java.name ->
                PupilSyncWorker(
                    appContext,
                    workerParameters,
                    syncService,
                )
            else -> null
        }
}
