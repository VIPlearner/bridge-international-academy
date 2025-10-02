package com.bridge.androidtechnicaltest.data.db.converters

import androidx.room.TypeConverter
import com.bridge.androidtechnicaltest.data.db.dto.SyncType

class SyncTypeConverter {

    @TypeConverter
    fun fromSyncType(syncType: SyncType?): String? {
        return syncType?.name
    }

    @TypeConverter
    fun toSyncType(syncTypeName: String?): SyncType? {
        return syncTypeName?.let { SyncType.valueOf(it) }
    }
}
