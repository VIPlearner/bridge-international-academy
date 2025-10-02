package com.bridge.androidtechnicaltest.data.repository

import com.bridge.androidtechnicaltest.data.db.AppDatabase
import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.data.network.PupilApi
import com.bridge.androidtechnicaltest.data.sync.PupilSyncManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PupilRepository @Inject constructor(
    private val database: AppDatabase,
    private val pupilApi: PupilApi,
    private val syncManager: PupilSyncManager
) : IPupilRepository {

    private val pupilDao = database.pupilDao

    override val pupils: Flow<List<Pupil>> = pupilDao.pupils

    override suspend fun addPupil(pupil: Pupil) {
        val pupilWithSync = pupil.copy(pendingSync = true, syncType = SyncType.ADD)
        pupilDao.upsert(pupilWithSync)
    }

    override suspend fun updatePupil(pupil: Pupil) {
        val pupilWithSync = pupil.copy(pendingSync = true, syncType = SyncType.UPDATE)
        pupilDao.upsert(pupilWithSync)
    }

    override suspend fun deletePupil(pupilId: Int) {
        pupilDao.markForSync(pupilId, SyncType.DELETE)
    }

    override fun startSync() {
        syncManager.startPeriodicSync()
    }

    override fun stopSync() {
        syncManager.stopPeriodicSync()
    }
}
