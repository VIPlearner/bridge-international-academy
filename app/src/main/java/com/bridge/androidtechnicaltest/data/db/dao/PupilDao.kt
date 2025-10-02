package com.bridge.androidtechnicaltest.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import kotlinx.coroutines.flow.Flow

@Dao
interface PupilDao {

    @Upsert
    suspend fun upsertAll(pupils: List<Pupil>)

    @get:Query("SELECT * FROM Pupils ORDER BY name ASC")
    val pupils: Flow<List<Pupil>>

    @Query("SELECT * FROM Pupils ORDER BY pupil_id ASC LIMIT :limit OFFSET :offset")
    fun getPupilsPage(limit: Int, offset: Int): List<Pupil>

    @Query("SELECT * FROM pupils WHERE pending_sync = 1")
    suspend fun getPendingSyncPupils(): List<Pupil>

    @Query("UPDATE pupils SET pending_sync = 0, sync_type = NULL WHERE pupil_id = :pupilId")
    suspend fun clearPendingSync(pupilId: Int)

    @Query("UPDATE pupils SET pending_sync = 1, sync_type = :syncType WHERE pupil_id = :pupilId")
    suspend fun markForSync(pupilId: Int, syncType: SyncType)

    @Query("DELETE FROM pupils")
    suspend fun clearAll()

    @Upsert
    suspend fun upsert(pupil: Pupil)
}