package com.bridge.androidtechnicaltest.domain.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PupilEntityTest {
    @Test
    fun pupilEntityWithDefaultIdReturnsMinusOne() {
        val pupilEntity =
            PupilEntity(
                name = "John Doe",
                country = "USA",
                image = "image.jpg",
                latitude = 40.7128,
                longitude = -74.0060,
            )

        assertEquals(0, pupilEntity.id)
    }

    @Test
    fun pupilEntityWithSpecificIdReturnsCorrectId() {
        val pupilEntity =
            PupilEntity(
                id = 123,
                name = "Jane Smith",
                country = "Canada",
                image = null,
                latitude = 45.4215,
                longitude = -75.6972,
            )

        assertEquals(123, pupilEntity.id)
    }

    @Test
    fun pupilEntityWithNullImageHandledCorrectly() {
        val pupilEntity =
            PupilEntity(
                id = 456,
                name = "Bob Johnson",
                country = "UK",
                image = null,
                latitude = 51.5074,
                longitude = -0.1278,
            )

        assertNull(pupilEntity.image)
        assertEquals("Bob Johnson", pupilEntity.name)
        assertEquals("UK", pupilEntity.country)
    }

    @Test
    fun pupilEntityEqualityWorksCorrectly() {
        val pupilEntity1 =
            PupilEntity(
                id = 1,
                name = "Test User",
                country = "Test Country",
                image = "test.jpg",
                latitude = 0.0,
                longitude = 0.0,
            )

        val pupilEntity2 =
            PupilEntity(
                id = 1,
                name = "Test User",
                country = "Test Country",
                image = "test.jpg",
                latitude = 0.0,
                longitude = 0.0,
            )

        val pupilEntity3 =
            PupilEntity(
                id = 2,
                name = "Test User",
                country = "Test Country",
                image = "test.jpg",
                latitude = 0.0,
                longitude = 0.0,
            )

        assertEquals(pupilEntity1, pupilEntity2)
        assertNotEquals(pupilEntity1, pupilEntity3)
    }

    @Test
    fun pupilEntityCopyWorksCorrectly() {
        val originalEntity =
            PupilEntity(
                id = 1,
                name = "Original Name",
                country = "Original Country",
                image = "original.jpg",
                latitude = 1.0,
                longitude = 2.0,
            )

        val copiedEntity = originalEntity.copy(name = "Updated Name")

        assertEquals(1, copiedEntity.id)
        assertEquals("Updated Name", copiedEntity.name)
        assertEquals("Original Country", copiedEntity.country)
        assertEquals("original.jpg", copiedEntity.image)
        assertEquals(1.0, copiedEntity.latitude, 0.0)
        assertEquals(2.0, copiedEntity.longitude, 0.0)
    }
}
