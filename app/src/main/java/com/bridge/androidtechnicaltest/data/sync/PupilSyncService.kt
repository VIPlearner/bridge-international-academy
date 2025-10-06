package com.bridge.androidtechnicaltest.data.sync

import com.bridge.androidtechnicaltest.data.datastore.DataStoreRepository
import com.bridge.androidtechnicaltest.data.db.dao.PupilDao
import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.data.mapper.toCreatePupilRequest
import com.bridge.androidtechnicaltest.data.mapper.toUpdatePupilRequest
import com.bridge.androidtechnicaltest.data.network.PupilApi
import com.bridge.androidtechnicaltest.domain.SyncState
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class PupilSyncService
    @Inject
    constructor(
        private val pupilDao: PupilDao,
        private val pupilApi: PupilApi,
        private val dataStoreRepository: DataStoreRepository,
    ) {
        private suspend fun isSyncing(): Boolean = dataStoreRepository.getPupilSyncState().first() == SyncState.SYNCING

        suspend fun sync(): Boolean {
            try {
                if (isSyncing()) {
                    Timber.d("Sync already in progress, skipping new sync request")
                    return false
                }
                Timber.d("PupilSyncWorker started")
                dataStoreRepository.setPupilSyncState(SyncState.SYNCING)

                val pupilsToSync = pupilDao.getPendingSyncPupils()

                var allSuccessful = true
                pupilsToSync.forEach {
                    val success = syncPupil(it)
                    allSuccessful = allSuccessful && success
                }
                if (!allSuccessful) {
                    dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE)
                    return false
                }

                if (!fetchAllPupilsFromApi()) {
                    dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE)
                    return false
                }

                dataStoreRepository.setPupilSyncState(SyncState.UP_TO_DATE)
                return true
            } catch (e: Exception) {
                dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE)
                return false
            }
        }

        private suspend fun fetchAllPupilsFromApi(): Boolean {
            pupilDao.deleteAll()
            val response = pupilApi.getPupils(page = 1)

            if (response.code() == 404) {
                // no pupils on server
                return true
            }

            if (!response.isSuccessful || response.body() == null) {
                return false
            }

            for (pupil in response.body()!!.items) {
                pupilDao.upsert(
                    Pupil(
                        remoteId = pupil.pupilId,
                        name = pupil.name,
                        country = pupil.country,
                        image = pupil.image,
                        latitude = pupil.latitude,
                        longitude = pupil.longitude,
                    ),
                )
            }

            for (index in 2..response.body()!!.totalPages) {
                val pagedResponse = pupilApi.getPupils(page = index)
                if (pagedResponse.code() == 404) {
                    // no more pupils on server
                    break
                }

                if (!pagedResponse.isSuccessful || pagedResponse.body() == null) {
                    return false
                }
                for (pupil in pagedResponse.body()!!.items) {
                    pupilDao.upsert(
                        Pupil(
                            remoteId = pupil.pupilId,
                            name = pupil.name,
                            country = pupil.country,
                            image = pupil.image,
                            latitude = pupil.latitude,
                            longitude = pupil.longitude,
                        ),
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
                    val updatedPupil =
                        pupil.copy(
                            remoteId = response.body()?.pupilId,
                            pendingSync = false,
                            syncType = null,
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
                            val updatedPupil =
                                pupil.copy(
                                    pendingSync = false,
                                    syncType = null,
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
