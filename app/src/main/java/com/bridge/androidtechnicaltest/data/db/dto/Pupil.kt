package com.bridge.androidtechnicaltest.data.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pupils")
data class Pupil(
    @PrimaryKey
    @ColumnInfo(name = "pupil_id")
    val pupilId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "image")
    val image: String?,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "pending_sync")
    val pendingSync: Boolean = false,

    @ColumnInfo(name = "sync_type")
    val syncType: SyncType? = null
)

// Simple wrapper for list of pupils (for backwards compatibility)
data class PupilList(
    val items: MutableList<Pupil>
)