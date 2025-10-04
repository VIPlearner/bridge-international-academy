package com.bridge.androidtechnicaltest.domain.entity

import org.junit.Assert.*
import org.junit.Test

class PupilWithLocationEntityTest {

    @Test
    fun createEntityWithAllFields() {
        val entity = PupilWithLocationEntity(
            id = 1,
            name = "John Doe",
            country = "USA",
            image = "john.jpg",
            latitude = 40.7128,
            longitude = -74.0060,
            prettyLocation = "New York"
        )

        assertEquals(1, entity.id)
        assertEquals("John Doe", entity.name)
        assertEquals("USA", entity.country)
        assertEquals("john.jpg", entity.image)
        assertEquals(40.7128, entity.latitude, 0.0)
        assertEquals(-74.0060, entity.longitude, 0.0)
        assertEquals("New York", entity.prettyLocation)
    }

    @Test
    fun createEntityWithNullValues() {
        val entity = PupilWithLocationEntity(
            id = 2,
            name = "Jane Smith",
            country = "Canada",
            image = null,
            latitude = 45.4215,
            longitude = -75.6972,
            prettyLocation = null
        )

        assertEquals(2, entity.id)
        assertEquals("Jane Smith", entity.name)
        assertEquals("Canada", entity.country)
        assertNull(entity.image)
        assertEquals(45.4215, entity.latitude, 0.0)
        assertEquals(-75.6972, entity.longitude, 0.0)
        assertNull(entity.prettyLocation)
    }

    @Test
    fun createEntityWithDefaultId() {
        val entity = PupilWithLocationEntity(
            name = "Test User",
            country = "Test Country",
            image = null,
            latitude = 0.0,
            longitude = 0.0
        )

        assertEquals(-1, entity.id)
        assertNull(entity.prettyLocation)
    }
}
