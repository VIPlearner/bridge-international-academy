package com.bridge.androidtechnicaltest.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
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

    @Query("SELECT * FROM pupils WHERE pending_sync = 1 OR sync_type IS NOT NULL")
    suspend fun getPendingSyncPupils(): List<Pupil>

    @Query("UPDATE pupils SET pending_sync = 1, sync_type = :syncType WHERE pupil_id = :pupilId")
    suspend fun markForSync(pupilId: Int, syncType: SyncType): Int

    @Upsert
    suspend fun upsert(pupil: Pupil)

    @Delete
    suspend fun delete(pupil: Pupil): Int

    @Query("UPDATE pupils SET name = :name, " +
            "country = :country, " +
            "image = :image, " +
            "latitude = :latitude, " +
            "longitude = :longitude " +
            "WHERE pupil_id = :pupilId")
    suspend fun updatePupilWithRemoteInfo(
        pupilId: Int,
        name: String,
        country: String,
        image: String?,
        latitude: Double,
        longitude: Double,
    ): Int


}