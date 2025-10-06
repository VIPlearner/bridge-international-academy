package com.bridge.androidtechnicaltest.data.db.converters

import androidx.room.TypeConverter
import com.bridge.androidtechnicaltest.data.db.dto.SyncType

class SyncTypeConverter {
    @TypeConverter
    fun fromSyncType(syncType: SyncType?): String? = syncType?.name

    @TypeConverter
    fun toSyncType(syncTypeName: String?): SyncType? = syncTypeName?.let { SyncType.valueOf(it) }
}
