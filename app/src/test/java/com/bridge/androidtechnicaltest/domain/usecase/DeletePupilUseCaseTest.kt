package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.utils.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeletePupilUseCaseTest {
    private lateinit var pupilRepository: IPupilRepository
    private lateinit var deletePupilUseCase: DeletePupilUseCase

    @Before
    fun setup() {
        pupilRepository = mockk()
        deletePupilUseCase = DeletePupilUseCase(pupilRepository)
    }

    @Test
    fun invokeSuccessfullyDeletesPupil() =
        runTest {
            val pupilId = 123

            coEvery { pupilRepository.deletePupil(pupilId) } just Runs

            val result = deletePupilUseCase(pupilId)

            assertTrue(result is Result.Success)
            coVerify { pupilRepository.deletePupil(pupilId) }
        }

    @Test
    fun invokeReturnsErrorWhenRepositoryThrowsException() =
        runTest {
            val pupilId = 456
            val expectedException = RuntimeException("Delete operation failed")

            coEvery { pupilRepository.deletePupil(pupilId) } throws expectedException

            val result = deletePupilUseCase(pupilId)

            assertTrue(result is Result.Error)
            assertEquals("Delete operation failed", (result as Result.Error).message)
            coVerify { pupilRepository.deletePupil(pupilId) }
        }

    @Test
    fun invokeReturnsGenericErrorWhenExceptionHasNoMessage() =
        runTest {
            val pupilId = 789
            val expectedException = RuntimeException()

            coEvery { pupilRepository.deletePupil(pupilId) } throws expectedException

            val result = deletePupilUseCase(pupilId)

            assertTrue(result is Result.Error)
            assertEquals("Unknown error occurred while deleting pupil", (result as Result.Error).message)
        }

    @Test
    fun invokeHandlesMultipleDeletionCalls() =
        runTest {
            val pupilId1 = 100
            val pupilId2 = 200

            coEvery { pupilRepository.deletePupil(pupilId1) } just Runs
            coEvery { pupilRepository.deletePupil(pupilId2) } just Runs

            val result1 = deletePupilUseCase(pupilId1)
            val result2 = deletePupilUseCase(pupilId2)

            assertTrue(result1 is Result.Success)
            assertTrue(result2 is Result.Success)
            coVerify { pupilRepository.deletePupil(pupilId1) }
            coVerify { pupilRepository.deletePupil(pupilId2) }
        }

    @Test
    fun invokeHandlesNegativePupilId() =
        runTest {
            val negativeId = -1

            coEvery { pupilRepository.deletePupil(negativeId) } just Runs

            val result = deletePupilUseCase(negativeId)

            assertTrue(result is Result.Success)
            coVerify { pupilRepository.deletePupil(negativeId) }
        }
}
