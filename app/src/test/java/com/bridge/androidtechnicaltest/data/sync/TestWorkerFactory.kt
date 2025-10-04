package com.bridge.androidtechnicaltest.data.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.bridge.androidtechnicaltest.data.datastore.DataStoreRepository
import com.bridge.androidtechnicaltest.data.db.AppDatabase
import com.bridge.androidtechnicaltest.data.network.PupilApi
import kotlin.jvm.java

class TestWorkerFactory(
    private val pupilApi: PupilApi,
    private val appDatabase: AppDatabase,
    private val dataStoreRepository: DataStoreRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            PupilSyncWorker::class.java.name -> PupilSyncWorker(
                appContext,
                workerParameters,
                appDatabase,
                pupilApi,
                dataStoreRepository
            )
            else -> null
        }
    }
}