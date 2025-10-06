package com.bridge.androidtechnicaltest.data.repository

import com.bridge.androidtechnicaltest.data.db.AppDatabase
import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.data.mapper.toCreatePupilRequest
import com.bridge.androidtechnicaltest.data.mapper.toUpdatePupilRequest
import com.bridge.androidtechnicaltest.data.network.PupilApi
import com.bridge.androidtechnicaltest.data.sync.PupilSyncManager
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PupilRepository
    @Inject
    constructor(
        private val database: AppDatabase,
        private val pupilApi: PupilApi,
        private val syncManager: PupilSyncManager,
    ) : IPupilRepository {
        private val pupilDao = database.pupilDao

        override val pupils: Flow<List<Pupil>>
            get() = pupilDao.pupils

        override suspend fun getPupilById(pupilId: Int): Pupil? = pupilDao.getPupilById(pupilId)

        override suspend fun addPupil(pupil: Pupil) {
            val res =
                try {
                    pupilApi.createPupil(pupil.toCreatePupilRequest())
                } catch (ex: Exception) {
                    Timber.e(ex, "Network error adding pupil")
                    null
                }
            Timber.d("Add pupil response: $res")
            if (res?.isSuccessful == true && res.body() != null) {
                val remotePupil = res.body()!!
                pupilDao.upsert(
                    pupil.copy(
                        remoteId = remotePupil.pupilId,
                        syncType = null,
                        pendingSync = false,
                    ),
                )
            } else if (res?.code() == 400) {
                Timber.d("Validation error adding pupil: ${res.errorBody()?.string()}")
                pupilDao.upsert(pupil.copy(syncType = SyncType.ADD, pendingSync = true))
            } else {
                // Treat as Server error, mark for add sync
                Timber.d("Server error adding pupil: ${res?.errorBody()?.string()}")
                pupilDao.upsert(pupil.copy(syncType = SyncType.ADD, pendingSync = true))
            }
        }

        override suspend fun updatePupil(pupil: Pupil) {
            if (pupil.remoteId != null) {
                val res =
                    try {
                        pupilApi.updatePupil(pupil.remoteId, pupil.toUpdatePupilRequest())
                    } catch (ex: Exception) {
                        Timber.e(ex, "Network error updating pupil")
                        null
                    }

                if (res?.isSuccessful == true && res.body() != null) {
                    val remotePupil = res.body()!!
                    pupilDao.upsert(
                        pupil.copy(
                            remoteId = remotePupil.pupilId,
                            syncType = null,
                            pendingSync = false,
                        ),
                    )
                } else if (res?.code() == 400) {
                    Timber.d("Validation error updating pupil: ${res.errorBody()?.string()}")
                    pupilDao.upsert(pupil.copy(syncType = SyncType.UPDATE, pendingSync = true))
                } else if (res?.code() == 404) {
                    Timber.d("Pupil not found remotely, adding as new pupil: ${res.errorBody()?.string()}")
                    addPupil(pupil)
                } else {
                    // Server error, mark for update sync
                    pupilDao.upsert(pupil.copy(syncType = SyncType.UPDATE, pendingSync = true))
                }
            } else {
                addPupil(pupil)
            }
        }

        override suspend fun deletePupil(pupilId: Int) {
            val pupil = pupilDao.getPupilById(pupilId)
            if (pupil != null) {
                if (pupil.remoteId != null) {
                    val res =
                        try {
                            pupilApi.deletePupil(pupil.remoteId)
                        } catch (ex: Exception) {
                            Timber.e(ex, "Network error deleting pupil")
                            null
                        }
                    if (res?.isSuccessful == true) {
                        pupilDao.deletePupilById(pupilId)
                    } else if (res?.code() == 404) {
                        // If not found remotely, consider it deleted
                        Timber.d("Pupil not found remotely when deleting, removing locally: ${res.errorBody()?.string()}")
                        pupilDao.deletePupilById(pupilId)
                    } else {
                        // Treat as server error, mark for delete sync
                        Timber.d("Server error deleting pupil: ${res?.errorBody()?.string()}")
                        pupilDao.markForSync(pupilId, SyncType.DELETE)
                    }
                } else {
                    pupilDao.deletePupilById(pupil.pupilId)
                }
            }
        }

        override fun startSync() {
            syncManager.startPeriodicSync()
        }

        override fun stopSync() {
            syncManager.stopPeriodicSync()
        }
    }
