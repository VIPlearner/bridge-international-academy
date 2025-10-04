package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.SyncType
import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetPupilsUseCaseTest {

    private lateinit var pupilRepository: IPupilRepository
    private lateinit var getPupilsUseCase: GetPupilsUseCase

    @Before
    fun setup() {
        pupilRepository = mockk()
        getPupilsUseCase = GetPupilsUseCase(pupilRepository)
    }

    @Test
    fun invokeReturnsEmptyListWhenRepositoryIsEmpty() = runTest {
        every { pupilRepository.pupils } returns flowOf(emptyList())

        val result = getPupilsUseCase().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun invokeReturnsMappedPupilEntitiesFromRepository() = runTest {
        val pupil1 = Pupil(
            pupilId = 1,
            name = "John Doe",
            country = "USA",
            image = "john.jpg",
            latitude = 40.7128,
            longitude = -74.0060,
            remoteId = 100,
            pendingSync = false,
            syncType = null
        )
        val pupil2 = Pupil(
            pupilId = 2,
            name = "Jane Smith",
            country = "Canada",
            image = null,
            latitude = 45.4215,
            longitude = -75.6972,
            remoteId = null,
            pendingSync = true,
            syncType = SyncType.ADD
        )
        val pupils = listOf(pupil1, pupil2)

        every { pupilRepository.pupils } returns flowOf(pupils)

        val result = getPupilsUseCase().first()

        assertEquals(2, result.size)

        val entity1 = result[0]
        assertEquals(1, entity1.id)
        assertEquals("John Doe", entity1.name)
        assertEquals("USA", entity1.country)
        assertEquals("john.jpg", entity1.image)
        assertEquals(40.7128, entity1.latitude, 0.0)
        assertEquals(-74.0060, entity1.longitude, 0.0)

        val entity2 = result[1]
        assertEquals(2, entity2.id)
        assertEquals("Jane Smith", entity2.name)
        assertEquals("Canada", entity2.country)
        assertNull(entity2.image)
        assertEquals(45.4215, entity2.latitude, 0.0)
        assertEquals(-75.6972, entity2.longitude, 0.0)
    }

    @Test
    fun invokeFiltersOutSyncFieldsFromDomainEntities() = runTest {
        val pupil = Pupil(
            pupilId = 999,
            name = "Test User",
            country = "Test Country",
            image = "test.jpg",
            latitude = 1.0,
            longitude = 2.0,
            remoteId = 888,
            pendingSync = true,
            syncType = SyncType.UPDATE
        )

        every { pupilRepository.pupils } returns flowOf(listOf(pupil))

        val result = getPupilsUseCase().first()

        assertEquals(1, result.size)
        val entity = result[0]
        assertEquals(999, entity.id)
        assertEquals("Test User", entity.name)
        assertEquals("Test Country", entity.country)
        assertEquals("test.jpg", entity.image)
        assertEquals(1.0, entity.latitude, 0.0)
        assertEquals(2.0, entity.longitude, 0.0)
    }
}
