package com.bridge.androidtechnicaltest.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.bridge.androidtechnicaltest.data.db.entities.LocationCacheEntity

@Dao
interface LocationCacheDao {

    @Upsert
    suspend fun upsert(locationCache: LocationCacheEntity)

    @Query("""
        SELECT * FROM location_cache 
        WHERE latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLng AND :maxLng
        ORDER BY timestamp DESC
    """)
    suspend fun findNearby(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<LocationCacheEntity>

    @Query("DELETE FROM location_cache WHERE timestamp < :cutoffTime")
    suspend fun deleteOldEntries(cutoffTime: Long)
}
