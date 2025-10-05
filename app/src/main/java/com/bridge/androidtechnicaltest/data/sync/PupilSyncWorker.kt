package com.bridge.androidtechnicaltest.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bridge.androidtechnicaltest.data.db.AppDatabase
import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.data.datastore.DataStoreRepository
import com.bridge.androidtechnicaltest.data.mapper.toCreatePupilRequest
import com.bridge.androidtechnicaltest.data.mapper.toUpdatePupilRequest
import com.bridge.androidtechnicaltest.data.network.PupilApi
import com.bridge.androidtechnicaltest.domain.SyncState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class PupilSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncService: PupilSyncService
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
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
