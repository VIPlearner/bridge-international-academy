package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.utils.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdatePupilUseCaseTest {
    private lateinit var pupilRepository: IPupilRepository
    private lateinit var updatePupilUseCase: UpdatePupilUseCase

    @Before
    fun setup() {
        pupilRepository = mockk()
        updatePupilUseCase = UpdatePupilUseCase(pupilRepository)
    }

    @Test
    fun invokeSuccessfullyUpdatesExistingPupil() =
        runTest {
            val existingPupil =
                Pupil(
                    pupilId = 1,
                    name = "Old Name",
                    country = "Old Country",
                    image = "old.jpg",
                    latitude = 1.0,
                    longitude = 2.0,
                    remoteId = 100,
                    pendingSync = true,
                    syncType = SyncType.ADD,
                )

            val updatedEntity =
                PupilEntity(
                    id = 1,
                    name = "New Name",
                    country = "New Country",
                    image = "new.jpg",
                    latitude = 3.0,
                    longitude = 4.0,
                )

            val expectedUpdatedPupil =
                existingPupil.copy(
                    name = "New Name",
                    country = "New Country",
                    image = "new.jpg",
                    latitude = 3.0,
                    longitude = 4.0,
                )

            coEvery { pupilRepository.getPupilById(1) } returns existingPupil
            coEvery { pupilRepository.updatePupil(expectedUpdatedPupil) } just Runs

            val result = updatePupilUseCase(updatedEntity)

            assertTrue(result is Result.Success)
            coVerify { pupilRepository.getPupilById(1) }
            coVerify { pupilRepository.updatePupil(expectedUpdatedPupil) }
        }

    @Test
    fun invokePreservesSyncFieldsFromExistingPupil() =
        runTest {
            val existingPupil =
                Pupil(
                    pupilId = 42,
                    name = "Original",
                    country = "Original Country",
                    image = null,
                    latitude = 10.0,
                    longitude = 20.0,
                    remoteId = 999,
                    pendingSync = false,
                    syncType = SyncType.UPDATE,
                )

            val updatedEntity =
                PupilEntity(
                    id = 42,
                    name = "Updated",
                    country = "Updated Country",
                    image = "updated.jpg",
                    latitude = 30.0,
                    longitude = 40.0,
                )

            val pupilSlot = slot<Pupil>()
            coEvery { pupilRepository.getPupilById(42) } returns existingPupil
            coEvery { pupilRepository.updatePupil(capture(pupilSlot)) } just Runs

            updatePupilUseCase(updatedEntity)

            val capturedPupil = pupilSlot.captured
            assertEquals(42, capturedPupil.pupilId)
            assertEquals("Updated", capturedPupil.name)
            assertEquals("Updated Country", capturedPupil.country)
            assertEquals("updated.jpg", capturedPupil.image)
            assertEquals(30.0, capturedPupil.latitude, 0.0)
            assertEquals(40.0, capturedPupil.longitude, 0.0)
            assertEquals(999, capturedPupil.remoteId)
            assertFalse(capturedPupil.pendingSync)
            assertEquals(SyncType.UPDATE, capturedPupil.syncType)
        }

    @Test
    fun invokeReturnsErrorWhenPupilNotFound() =
        runTest {
            val updatedEntity =
                PupilEntity(
                    id = 999,
                    name = "Non-existent",
                    country = "Nowhere",
                    image = null,
                    latitude = 0.0,
                    longitude = 0.0,
                )

            coEvery { pupilRepository.getPupilById(999) } returns null

            val result = updatePupilUseCase(updatedEntity)

            assertTrue(result is Result.Error)
            coVerify { pupilRepository.getPupilById(999) }
            coVerify(exactly = 0) { pupilRepository.updatePupil(any()) }
        }

    @Test
    fun invokeReturnsErrorWhenRepositoryGetThrowsException() =
        runTest {
            val updatedEntity =
                PupilEntity(
                    id = 123,
                    name = "Test",
                    country = "Test Country",
                    image = "test.jpg",
                    latitude = 5.0,
                    longitude = 6.0,
                )

            val expectedException = RuntimeException("Database connection error")
            coEvery { pupilRepository.getPupilById(123) } throws expectedException

            val result = updatePupilUseCase(updatedEntity)

            assertTrue(result is Result.Error)
            assertEquals("Database connection error", (result as Result.Error).message)
        }

    @Test
    fun invokeReturnsErrorWhenRepositoryUpdateThrowsException() =
        runTest {
            val existingPupil =
                Pupil(
                    pupilId = 456,
                    name = "Existing",
                    country = "Existing Country",
                    image = "existing.jpg",
                    latitude = 7.0,
                    longitude = 8.0,
                    remoteId = null,
                    pendingSync = false,
                    syncType = null,
                )

            val updatedEntity =
                PupilEntity(
                    id = 456,
                    name = "Updated",
                    country = "Updated Country",
                    image = "updated.jpg",
                    latitude = 9.0,
                    longitude = 10.0,
                )

            val expectedException = RuntimeException("Update failed")
            coEvery { pupilRepository.getPupilById(456) } returns existingPupil
            coEvery { pupilRepository.updatePupil(any()) } throws expectedException

            val result = updatePupilUseCase(updatedEntity)

            assertTrue(result is Result.Error)
            assertEquals("Update failed", (result as Result.Error).message)
        }

    @Test
    fun invokeReturnsGenericErrorWhenExceptionHasNoMessage() =
        runTest {
            val updatedEntity =
                PupilEntity(
                    id = 789,
                    name = "Test",
                    country = "Test",
                    image = null,
                    latitude = 0.0,
                    longitude = 0.0,
                )

            val expectedException = RuntimeException()
            coEvery { pupilRepository.getPupilById(789) } throws expectedException

            val result = updatePupilUseCase(updatedEntity)

            assertTrue(result is Result.Error)
            assertEquals("Unknown error occurred while updating pupil", (result as Result.Error).message)
        }
}
