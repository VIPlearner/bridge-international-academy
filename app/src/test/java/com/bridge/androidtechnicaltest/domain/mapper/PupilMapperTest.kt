package com.bridge.androidtechnicaltest.domain.mapper

import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PupilMapperTest {
    @Test
    fun pupilToDomainEntityMapsCorrectly() {
        val pupil =
            Pupil(
                pupilId = 123,
                name = "John Doe",
                country = "USA",
                image = "profile.jpg",
                latitude = 40.7128,
                longitude = -74.0060,
                remoteId = 456,
                pendingSync = true,
                syncType = SyncType.UPDATE,
            )

        val domainEntity = pupil.toDomainEntity()

        assertEquals(123, domainEntity.id)
        assertEquals("John Doe", domainEntity.name)
        assertEquals("USA", domainEntity.country)
        assertEquals("profile.jpg", domainEntity.image)
        assertEquals(40.7128, domainEntity.latitude, 0.0)
        assertEquals(-74.0060, domainEntity.longitude, 0.0)
    }

    @Test
    fun pupilToDomainEntityWithNullImageMapsCorrectly() {
        val pupil =
            Pupil(
                pupilId = 789,
                name = "Jane Smith",
                country = "Canada",
                image = null,
                latitude = 45.4215,
                longitude = -75.6972,
                remoteId = null,
                pendingSync = false,
                syncType = null,
            )

        val domainEntity = pupil.toDomainEntity()

        assertEquals(789, domainEntity.id)
        assertEquals("Jane Smith", domainEntity.name)
        assertEquals("Canada", domainEntity.country)
        assertNull(domainEntity.image)
        assertEquals(45.4215, domainEntity.latitude, 0.0)
        assertEquals(-75.6972, domainEntity.longitude, 0.0)
    }

    @Test
    fun pupilEntityToNewPupilWithDefaultIdMapsCorrectly() {
        val pupilEntity =
            PupilEntity(
                name = "New User",
                country = "Germany",
                image = "new.jpg",
                latitude = 52.5200,
                longitude = 13.4050,
            )

        val newPupil = pupilEntity.toNewPupil()

        assertEquals(0, newPupil.pupilId)
        assertEquals("New User", newPupil.name)
        assertEquals("Germany", newPupil.country)
        assertEquals("new.jpg", newPupil.image)
        assertEquals(52.5200, newPupil.latitude, 0.0)
        assertEquals(13.4050, newPupil.longitude, 0.0)
        assertNull(newPupil.remoteId)
        assertFalse(newPupil.pendingSync)
        assertNull(newPupil.syncType)
    }

    @Test
    fun pupilEntityToNewPupilWithSpecificIdMapsCorrectly() {
        val pupilEntity =
            PupilEntity(
                id = 42,
                name = "Existing User",
                country = "France",
                image = null,
                latitude = 48.8566,
                longitude = 2.3522,
            )

        val newPupil = pupilEntity.toNewPupil()

        assertEquals(42, newPupil.pupilId)
        assertEquals("Existing User", newPupil.name)
        assertEquals("France", newPupil.country)
        assertNull(newPupil.image)
        assertEquals(48.8566, newPupil.latitude, 0.0)
        assertEquals(2.3522, newPupil.longitude, 0.0)
        assertNull(newPupil.remoteId)
        assertFalse(newPupil.pendingSync)
        assertNull(newPupil.syncType)
    }

    @Test
    fun pupilEntityToUpdatedPupilPreservesExistingSyncFields() {
        val existingPupil =
            Pupil(
                pupilId = 100,
                name = "Old Name",
                country = "Old Country",
                image = "old.jpg",
                latitude = 1.0,
                longitude = 2.0,
                remoteId = 999,
                pendingSync = true,
                syncType = SyncType.ADD,
            )

        val updatedEntity =
            PupilEntity(
                id = 100,
                name = "Updated Name",
                country = "Updated Country",
                image = "updated.jpg",
                latitude = 3.0,
                longitude = 4.0,
            )

        val updatedPupil = updatedEntity.toUpdatedPupil(existingPupil)

        assertEquals(100, updatedPupil.pupilId)
        assertEquals("Updated Name", updatedPupil.name)
        assertEquals("Updated Country", updatedPupil.country)
        assertEquals("updated.jpg", updatedPupil.image)
        assertEquals(3.0, updatedPupil.latitude, 0.0)
        assertEquals(4.0, updatedPupil.longitude, 0.0)
        assertEquals(999, updatedPupil.remoteId)
        assertTrue(updatedPupil.pendingSync)
        assertEquals(SyncType.ADD, updatedPupil.syncType)
    }

    @Test
    fun pupilEntityToUpdatedPupilWithNullImageHandledCorrectly() {
        val existingPupil =
            Pupil(
                pupilId = 200,
                name = "Test User",
                country = "Test Country",
                image = "test.jpg",
                latitude = 5.0,
                longitude = 6.0,
                remoteId = 888,
                pendingSync = false,
                syncType = SyncType.UPDATE,
            )

        val updatedEntity =
            PupilEntity(
                id = 200,
                name = "Updated Test User",
                country = "Updated Test Country",
                image = null,
                latitude = 7.0,
                longitude = 8.0,
            )

        val updatedPupil = updatedEntity.toUpdatedPupil(existingPupil)

        assertEquals(200, updatedPupil.pupilId)
        assertEquals("Updated Test User", updatedPupil.name)
        assertEquals("Updated Test Country", updatedPupil.country)
        assertNull(updatedPupil.image)
        assertEquals(7.0, updatedPupil.latitude, 0.0)
        assertEquals(8.0, updatedPupil.longitude, 0.0)
        assertEquals(888, updatedPupil.remoteId)
        assertFalse(updatedPupil.pendingSync)
        assertEquals(SyncType.UPDATE, updatedPupil.syncType)
    }
}
