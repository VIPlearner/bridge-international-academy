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

@HiltWorker
class PupilSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    database: AppDatabase,
    private val pupilApi: PupilApi,
    private val dataStoreRepository: DataStoreRepository
) : CoroutineWorker(context, workerParams) {

    private val pupilDao = database.pupilDao

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            dataStoreRepository.setPupilSyncState(SyncState.SYNCING)

            val pupilsToSync = pupilDao.getPendingSyncPupils()

            var allSuccessful = true
            pupilsToSync.forEach {
                val success = syncPupil(it)
                allSuccessful = allSuccessful && success
            }
            if (!allSuccessful) {
                dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE)
                return@withContext Result.retry()
            }

            if (!fetchAllPupilsFromApi()) {
                dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE)
                return@withContext Result.retry()
            }

            dataStoreRepository.setPupilSyncState(SyncState.UP_TO_DATE)
            Result.success()
        } catch (e: Exception) {
            dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE)
            Result.retry()
        }
    }

    private suspend fun fetchAllPupilsFromApi(): Boolean {
        val response = pupilApi.getPupils(page = 1)

        if (!response.isSuccessful || response.body() == null) {
            return false
        }

        for (pupil in response.body()!!.items) {
            pupilDao.updatePupilWithRemoteInfo(
                pupilId = pupil.pupilId,
                name = pupil.name,
                country = pupil.country,
                image = pupil.image,
                latitude = pupil.latitude,
                longitude = pupil.longitude
            )
        }

        for (index in 2..response.body()!!.totalPages) {
            val pagedResponse = pupilApi.getPupils(page = index)
            if (!pagedResponse.isSuccessful || pagedResponse.body() == null) {
                return false
            }
            for (pupil in pagedResponse.body()!!.items) {
                pupilDao.updatePupilWithRemoteInfo(
                    pupilId = pupil.pupilId,
                    name = pupil.name,
                    country = pupil.country,
                    image = pupil.image,
                    latitude = pupil.latitude,
                    longitude = pupil.longitude
                )
            }
        }
        return true
    }

    private suspend fun syncPupil(pupil: Pupil): Boolean {
        if (!pupil.pendingSync || pupil.syncType == null) {
            return true // No sync needed
        }

        suspend fun addPupil(pupil: Pupil): Boolean {
            val response = pupilApi.createPupil(pupil.toCreatePupilRequest())
            if (response.isSuccessful) {
                val updatedPupil = pupil.copy(
                    remoteId = response.body()?.pupilId,
                    pendingSync = false,
                    syncType = null
                )
                pupilDao.upsert(updatedPupil)
                return true
            } else {
                return false
            }

        }

        try {
            when (pupil.syncType) {
                SyncType.ADD -> {
                    return addPupil(pupil)
                }
                SyncType.UPDATE -> {
                    val remoteId = pupil.remoteId ?: return addPupil(pupil)
                    val response = pupilApi.updatePupil(remoteId, pupil.toUpdatePupilRequest())
                    return if (response.isSuccessful) {
                        val updatedPupil = pupil.copy(
                            pendingSync = false,
                            syncType = null
                        )
                        pupilDao.upsert(updatedPupil)
                        true
                    } else {
                        false
                    }
                }
                SyncType.DELETE -> {
                    val remoteId = pupil.remoteId ?: return true // If no remote ID, consider it deleted
                    val response = pupilApi.deletePupil(remoteId)
                    return if (response.isSuccessful) {
                        pupilDao.delete(pupil)
                        true
                    } else {
                        false
                    }
                }
            }
        } catch (e: Exception) {

            return false
        }
    }
}
