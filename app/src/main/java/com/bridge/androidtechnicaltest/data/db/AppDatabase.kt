package com.bridge.androidtechnicaltest.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bridge.androidtechnicaltest.data.db.converters.SyncTypeConverter
import com.bridge.androidtechnicaltest.data.db.dao.LocationCacheDao
import com.bridge.androidtechnicaltest.data.db.dao.PupilDao
import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.entities.LocationCacheEntity

@Database(entities = [Pupil::class, LocationCacheEntity::class], version = 3, exportSchema = false)
@TypeConverters(SyncTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val pupilDao: PupilDao
    abstract val locationCacheDao: LocationCacheDao
}