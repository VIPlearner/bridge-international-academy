package com.bridge.androidtechnicaltest.utils

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class LocationUtilsTest {

    @Test
    fun boundingBoxCalculatesCorrectBounds() {
        val lat = 40.7128
        val lng = -74.0060
        val radiusKm = 1.0

        val result = boundingBox(lat, lng, radiusKm)

        assertTrue(result.minLat < lat)
        assertTrue(result.maxLat > lat)
        assertTrue(result.minLng < lng)
        assertTrue(result.maxLng > lng)

        val latDiff = result.maxLat - result.minLat
        val lngDiff = result.maxLng - result.minLng
        assertTrue(latDiff > 0)
        assertTrue(lngDiff > 0)
    }

    @Test
    fun boundingBoxWithZeroRadiusReturnsPoint() {
        val lat = 40.7128
        val lng = -74.0060
        val radiusKm = 0.0

        val result = boundingBox(lat, lng, radiusKm)

        assertEquals(lat, result.minLat, 0.0001)
        assertEquals(lat, result.maxLat, 0.0001)
        assertEquals(lng, result.minLng, 0.0001)
        assertEquals(lng, result.maxLng, 0.0001)
    }

    @Test
    fun distanceKmCalculatesCorrectDistance() {
        val lat1 = 40.7128
        val lng1 = -74.0060
        val lat2 = 40.7589
        val lng2 = -73.9851

        val result = distanceKm(lat1, lng1, lat2, lng2)

        assertTrue(result > 0)
        assertTrue(result < 10)
        assertEquals(result, 5.42, 0.1)
    }

    @Test
    fun distanceKmReturnZeroForSamePoints() {
        val lat = 40.7128
        val lng = -74.0060

        val result = distanceKm(lat, lng, lat, lng)

        assertEquals(0.0, result, 0.0001)
    }

    @Test
    fun distanceKmIsSymmetric() {
        val lat1 = 40.7128
        val lng1 = -74.0060
        val lat2 = 40.7589
        val lng2 = -73.9851

        val distance1 = distanceKm(lat1, lng1, lat2, lng2)
        val distance2 = distanceKm(lat2, lng2, lat1, lng1)

        assertEquals(distance1, distance2, 0.0001)
    }

    @Test
    fun distanceKmHandlesLongDistances() {
        val nyLat = 40.7128
        val nyLng = -74.0060
        val laLat = 34.0522
        val laLng = -118.2437

        val result = distanceKm(nyLat, nyLng, laLat, laLng)

        assertTrue(result > 3900)
        assertTrue(result < 4000)
    }
}
