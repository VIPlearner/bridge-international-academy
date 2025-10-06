package com.bridge.androidtechnicaltest.data.repository

import com.bridge.androidtechnicaltest.data.db.dao.LocationCacheDao
import com.bridge.androidtechnicaltest.data.db.entities.LocationCacheEntity
import com.bridge.androidtechnicaltest.data.network.GeocodingApi
import com.bridge.androidtechnicaltest.utils.boundingBox
import com.bridge.androidtechnicaltest.utils.distanceKm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationResolver
    @Inject
    constructor(
        private val locationCacheDao: LocationCacheDao,
        private val geocodingApi: GeocodingApi,
    ) {
        companion object {
            private const val CACHE_RADIUS_KM = 1.0
            private const val CACHE_RETENTION_DAYS = 30
            private const val HOURS_PER_DAY = 24
            private const val MINUTES_PER_HOUR = 60
            private const val SECONDS_PER_MINUTE = 60
            private const val MILLISECONDS_PER_SECOND = 1000L
        }

        /**
         * Resolve latitude/longitude coordinates to a human-readable city name.
         * Uses cached results when available within 1km radius, otherwise calls the API.
         *
         * @param lat Latitude coordinate
         * @param lng Longitude coordinate
         * @return City name or null if resolution fails
         */
        suspend fun getPrettyLocation(
            lat: Double,
            lng: Double,
        ): String? =
            withContext(Dispatchers.IO) {
                try {
                    val cachedLocation = findNearbyInCache(lat, lng)
                    if (cachedLocation != null) {
                        Timber.d("Found cached location: ${cachedLocation.cityName}")
                        return@withContext cachedLocation.cityName
                    }

                    val cityName = resolveFromApi(lat, lng)
                    if (cityName != null) {
                        cacheLocation(lat, lng, cityName)
                        Timber.d("Resolved and cached new location: $cityName")
                        return@withContext cityName
                    }

                    Timber.w("Failed to resolve location for coordinates: ($lat, $lng)")
                    return@withContext null
                } catch (e: Exception) {
                    Timber.e(e, "Error resolving location for coordinates: ($lat, $lng)")
                    return@withContext null
                }
            }

        private suspend fun findNearbyInCache(
            lat: Double,
            lng: Double,
        ): LocationCacheEntity? {
            val boundingBox = boundingBox(lat, lng, CACHE_RADIUS_KM)

            val nearbyLocations =
                locationCacheDao.findNearby(
                    minLat = boundingBox.minLat,
                    maxLat = boundingBox.maxLat,
                    minLng = boundingBox.minLng,
                    maxLng = boundingBox.maxLng,
                )

            return nearbyLocations.firstOrNull { cached ->
                val distance = distanceKm(lat, lng, cached.latitude, cached.longitude)
                distance <= CACHE_RADIUS_KM
            }
        }

        private suspend fun resolveFromApi(
            lat: Double,
            lng: Double,
        ): String? =
            try {
                val response =
                    geocodingApi.reverseGeocode(
                        latitude = lat,
                        longitude = lng,
                        limit = 1,
                    )

                if (response.isSuccessful) {
                    val locations = response.body()
                    if (!locations.isNullOrEmpty()) {
                        val location = locations.first()
                        listOf(location.name, location.state, location.country)
                            .filter { !it.isNullOrBlank() }
                            .joinToString(", ")
                    } else {
                        Timber.w("Empty response from geocoding API")
                        null
                    }
                } else {
                    Timber.e("Geocoding API error: ${response.code()} - ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Network error calling geocoding API")
                null
            }

        private suspend fun cacheLocation(
            lat: Double,
            lng: Double,
            cityName: String,
        ) {
            try {
                val cacheEntity =
                    LocationCacheEntity(
                        latitude = lat,
                        longitude = lng,
                        cityName = cityName,
                    )
                locationCacheDao.upsert(cacheEntity)
            } catch (e: Exception) {
                Timber.e(e, "Error caching location")
            }
        }

        /**
         * Clean up old cache entries (older than 30 days)
         */
        suspend fun cleanupOldCache() {
            try {
                val thirtyDaysAgo =
                    System.currentTimeMillis() -
                        (
                            CACHE_RETENTION_DAYS * HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE
                                * MILLISECONDS_PER_SECOND
                        )
                locationCacheDao.deleteOldEntries(thirtyDaysAgo)
            } catch (e: Exception) {
                Timber.e(e, "Error cleaning up old cache")
            }
        }
    }
