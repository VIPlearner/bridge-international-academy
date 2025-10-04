package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.utils.Result
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AddPupilUseCaseTest {

    private lateinit var pupilRepository: IPupilRepository
    private lateinit var addPupilUseCase: AddPupilUseCase

    @Before
    fun setup() {
        pupilRepository = mockk()
        addPupilUseCase = AddPupilUseCase(pupilRepository)
    }

    @Test
    fun invokeSuccessfullyAddsNewPupilWithDefaultId() = runTest {
        val pupilEntity = PupilEntity(
            name = "John Doe",
            country = "USA",
            image = "john.jpg",
            latitude = 40.7128,
            longitude = -74.0060
        )

        val expectedPupil = Pupil(
            pupilId = -1,
            name = "John Doe",
            country = "USA",
            image = "john.jpg",
            latitude = 40.7128,
            longitude = -74.0060,
            remoteId = null,
            pendingSync = false,
            syncType = null
        )

        coEvery { pupilRepository.addPupil(expectedPupil) } just Runs

        val result = addPupilUseCase(pupilEntity)

        assertTrue(result is Result.Success)
        coVerify { pupilRepository.addPupil(expectedPupil) }
    }

    @Test
    fun invokeSuccessfullyAddsNewPupilWithSpecificId() = runTest {
        val pupilEntity = PupilEntity(
            id = 42,
            name = "Jane Smith",
            country = "Canada",
            image = null,
            latitude = 45.4215,
            longitude = -75.6972
        )

        val expectedPupil = Pupil(
            pupilId = 42,
            name = "Jane Smith",
            country = "Canada",
            image = null,
            latitude = 45.4215,
            longitude = -75.6972,
            remoteId = null,
            pendingSync = false,
            syncType = null
        )

        coEvery { pupilRepository.addPupil(expectedPupil) } just Runs

        val result = addPupilUseCase(pupilEntity)

        assertTrue(result is Result.Success)
        coVerify { pupilRepository.addPupil(expectedPupil) }
    }

    @Test
    fun invokeReturnsErrorWhenRepositoryThrowsException() = runTest {
        val pupilEntity = PupilEntity(
            name = "Test User",
            country = "Test Country",
            image = "test.jpg",
            latitude = 1.0,
            longitude = 2.0
        )

        val expectedException = RuntimeException("Database error")
        coEvery { pupilRepository.addPupil(any()) } throws expectedException

        val result = addPupilUseCase(pupilEntity)

        assertTrue(result is Result.Error)
        assertEquals("Database error", (result as Result.Error).message)
    }

    @Test
    fun invokeReturnsGenericErrorWhenExceptionHasNoMessage() = runTest {
        val pupilEntity = PupilEntity(
            name = "Test User",
            country = "Test Country",
            image = null,
            latitude = 0.0,
            longitude = 0.0
        )

        val expectedException = RuntimeException()
        coEvery { pupilRepository.addPupil(any()) } throws expectedException

        val result = addPupilUseCase(pupilEntity)

        assertTrue(result is Result.Error)
        assertEquals("Unknown error occurred while adding pupil", (result as Result.Error).message)
    }

    @Test
    fun invokeEnsuresSyncFieldsAreSetToDefaults() = runTest {
        val pupilEntity = PupilEntity(
            id = 100,
            name = "Sync Test",
            country = "Sync Country",
            image = "sync.jpg",
            latitude = 50.0,
            longitude = 60.0
        )

        val pupilSlot = slot<Pupil>()
        coEvery { pupilRepository.addPupil(capture(pupilSlot)) } just Runs

        addPupilUseCase(pupilEntity)

        val capturedPupil = pupilSlot.captured
        assertNull(capturedPupil.remoteId)
        assertFalse(capturedPupil.pendingSync)
        assertNull(capturedPupil.syncType)
    }
}
