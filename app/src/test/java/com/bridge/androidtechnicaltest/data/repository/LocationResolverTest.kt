package com.bridge.androidtechnicaltest.data.repository

import com.bridge.androidtechnicaltest.data.db.AppDatabase
import com.bridge.androidtechnicaltest.data.db.dao.LocationCacheDao
import com.bridge.androidtechnicaltest.data.db.entities.LocationCacheEntity
import com.bridge.androidtechnicaltest.data.network.GeocodingApi
import com.bridge.androidtechnicaltest.data.network.dto.GeocodingResponse
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class LocationResolverTest {
    private lateinit var appDatabase: AppDatabase
    private lateinit var locationCacheDao: LocationCacheDao
    private lateinit var geocodingApi: GeocodingApi
    private lateinit var locationResolver: LocationResolver

    @Before
    fun setup() {
        appDatabase = mockk()
        locationCacheDao = mockk()
        geocodingApi = mockk()
        locationResolver = LocationResolver(locationCacheDao, geocodingApi)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun getPrettyLocationReturnsCachedLocationWhenWithinRadius() =
        runTest {
            val lat = 40.7128
            val lng = -74.0060
            val cachedEntity =
                LocationCacheEntity(
                    id = 1,
                    latitude = 40.7129,
                    longitude = -74.0061,
                    cityName = "New York",
                    timestamp = System.currentTimeMillis(),
                )

            coEvery { locationCacheDao.findNearby(any(), any(), any(), any()) } returns listOf(cachedEntity)

            val result = locationResolver.getPrettyLocation(lat, lng)

            assertEquals("New York", result)
            coVerify { locationCacheDao.findNearby(any(), any(), any(), any()) }
            coVerify(exactly = 0) { geocodingApi.reverseGeocode(any(), any(), any(), any()) }
        }

    @Test
    fun getPrettyLocationCallsApiWhenNoCacheHit() =
        runTest {
            val lat = 40.7128
            val lng = -74.0060
            val geocodingResponse =
                GeocodingResponse(
                    name = "New York",
                    localNames = null,
                    latitude = lat,
                    longitude = lng,
                    country = "US",
                    state = "NY",
                )

            coEvery { locationCacheDao.findNearby(any(), any(), any(), any()) } returns emptyList()
            coEvery { geocodingApi.reverseGeocode(lat, lng, 1, any()) } returns Response.success(listOf(geocodingResponse))
            coEvery { locationCacheDao.upsert(any()) } just Runs

            val result = locationResolver.getPrettyLocation(lat, lng)

            assertEquals("New York, NY, US", result)
            coVerify { locationCacheDao.findNearby(any(), any(), any(), any()) }
            coVerify { geocodingApi.reverseGeocode(lat, lng, 1, any()) }
            coVerify { locationCacheDao.upsert(any()) }
        }

    @Test
    fun getPrettyLocationCachesApiResult() =
        runTest {
            val lat = 40.7128
            val lng = -74.0060
            val geocodingResponse =
                GeocodingResponse(
                    name = "New York",
                    localNames = null,
                    latitude = lat,
                    longitude = lng,
                    country = "US",
                    state = "NY",
                )
            val cachedEntitySlot = slot<LocationCacheEntity>()

            coEvery { locationCacheDao.findNearby(any(), any(), any(), any()) } returns emptyList()
            coEvery { geocodingApi.reverseGeocode(lat, lng, 1, any()) } returns Response.success(listOf(geocodingResponse))
            coEvery { locationCacheDao.upsert(capture(cachedEntitySlot)) } just Runs

            locationResolver.getPrettyLocation(lat, lng)

            val cachedEntity = cachedEntitySlot.captured
            assertEquals(lat, cachedEntity.latitude, 0.0001)
            assertEquals(lng, cachedEntity.longitude, 0.0001)
            assertEquals("New York, NY, US", cachedEntity.cityName)
        }

    @Test
    fun getPrettyLocationReturnsNullWhenApiReturnsEmptyList() =
        runTest {
            val lat = 40.7128
            val lng = -74.0060

            coEvery { locationCacheDao.findNearby(any(), any(), any(), any()) } returns emptyList()
            coEvery { geocodingApi.reverseGeocode(lat, lng, 1, any()) } returns Response.success(emptyList())

            val result = locationResolver.getPrettyLocation(lat, lng)

            assertNull(result)
            coVerify(exactly = 0) { locationCacheDao.upsert(any()) }
        }

    @Test
    fun getPrettyLocationReturnsNullWhenApiCallFails() =
        runTest {
            val lat = 40.7128
            val lng = -74.0060

            coEvery { locationCacheDao.findNearby(any(), any(), any(), any()) } returns emptyList()
            coEvery { geocodingApi.reverseGeocode(lat, lng, 1, any()) } returns Response.error(404, "".toByteArray().toResponseBody())

            val result = locationResolver.getPrettyLocation(lat, lng)

            assertNull(result)
            coVerify(exactly = 0) { locationCacheDao.upsert(any()) }
        }

    @Test
    fun getPrettyLocationFallsBackToStateWhenNameIsBlank() =
        runTest {
            val lat = 40.7128
            val lng = -74.0060
            val geocodingResponse =
                GeocodingResponse(
                    name = "",
                    localNames = null,
                    latitude = lat,
                    longitude = lng,
                    country = "US",
                    state = "New York",
                )

            coEvery { locationCacheDao.findNearby(any(), any(), any(), any()) } returns emptyList()
            coEvery { geocodingApi.reverseGeocode(lat, lng, 1, any()) } returns Response.success(listOf(geocodingResponse))
            coEvery { locationCacheDao.upsert(any()) } just Runs

            val result = locationResolver.getPrettyLocation(lat, lng)

            assertEquals("New York, US", result)
        }

    @Test
    fun getPrettyLocationFallsBackToCountryWhenNameAndStateAreBlank() =
        runTest {
            val lat = 40.7128
            val lng = -74.0060
            val geocodingResponse =
                GeocodingResponse(
                    name = "",
                    localNames = null,
                    latitude = lat,
                    longitude = lng,
                    country = "United States",
                    state = "",
                )

            coEvery { locationCacheDao.findNearby(any(), any(), any(), any()) } returns emptyList()
            coEvery { geocodingApi.reverseGeocode(lat, lng, 1, any()) } returns Response.success(listOf(geocodingResponse))
            coEvery { locationCacheDao.upsert(any()) } just Runs

            val result = locationResolver.getPrettyLocation(lat, lng)

            assertEquals("United States", result)
        }

    @Test
    fun getPrettyLocationIgnoresCachedLocationOutsideRadius() =
        runTest {
            val lat = 40.7128
            val lng = -74.0060
            val farCachedEntity =
                LocationCacheEntity(
                    id = 1,
                    latitude = 40.8000,
                    longitude = -74.1000,
                    cityName = "Far Location",
                    timestamp = System.currentTimeMillis(),
                )
            val geocodingResponse =
                GeocodingResponse(
                    name = "New York",
                    localNames = null,
                    latitude = lat,
                    longitude = lng,
                    country = "US",
                    state = "NY",
                )

            coEvery { locationCacheDao.findNearby(any(), any(), any(), any()) } returns listOf(farCachedEntity)
            coEvery { geocodingApi.reverseGeocode(lat, lng, 1, any()) } returns Response.success(listOf(geocodingResponse))
            coEvery { locationCacheDao.upsert(any()) } just Runs

            val result = locationResolver.getPrettyLocation(lat, lng)

            assertEquals("New York, NY, US", result)
            coVerify { geocodingApi.reverseGeocode(lat, lng, 1, any()) }
        }

    @Test
    fun getPrettyLocationHandlesExceptionGracefully() =
        runTest {
            val lat = 40.7128
            val lng = -74.0060

            coEvery { locationCacheDao.findNearby(any(), any(), any(), any()) } throws RuntimeException("Database error")

            val result = locationResolver.getPrettyLocation(lat, lng)

            assertNull(result)
        }

    @Test
    fun cleanupOldCacheDeletesOldEntries() =
        runTest {
            coEvery { locationCacheDao.deleteOldEntries(any()) } just Runs

            locationResolver.cleanupOldCache()

            coVerify { locationCacheDao.deleteOldEntries(any()) }
        }

    @Test
    fun cleanupOldCacheHandlesExceptionGracefully() =
        runTest {
            coEvery { locationCacheDao.deleteOldEntries(any()) } throws RuntimeException("Database error")

            locationResolver.cleanupOldCache()

            coVerify { locationCacheDao.deleteOldEntries(any()) }
        }
}
