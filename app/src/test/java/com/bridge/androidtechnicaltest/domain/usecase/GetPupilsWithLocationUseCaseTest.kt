package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.data.repository.LocationResolver
import com.bridge.androidtechnicaltest.domain.entity.PupilWithLocationEntity
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetPupilsWithLocationUseCaseTest {

    private lateinit var pupilRepository: IPupilRepository
    private lateinit var locationResolver: LocationResolver
    private lateinit var getPupilsWithLocationUseCase: GetPupilsWithLocationUseCase

    @Before
    fun setup() {
        pupilRepository = mockk()
        locationResolver = mockk()
        getPupilsWithLocationUseCase = GetPupilsWithLocationUseCase(pupilRepository, locationResolver)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invokeReturnsEmptyListWhenRepositoryIsEmpty() = runTest {
        every { pupilRepository.pupils } returns flowOf(emptyList())

        val result = getPupilsWithLocationUseCase().first()

        assertTrue(result.isEmpty())
        coVerify(exactly = 0) { locationResolver.getPrettyLocation(any(), any()) }
    }

    @Test
    fun invokeReturnsPupilsWithResolvedLocations() = runTest {
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
            syncType = null
        )
        val pupils = listOf(pupil1, pupil2)

        every { pupilRepository.pupils } returns flowOf(pupils)
        coEvery { locationResolver.getPrettyLocation(40.7128, -74.0060) } returns "New York"
        coEvery { locationResolver.getPrettyLocation(45.4215, -75.6972) } returns "Ottawa"

        val result = getPupilsWithLocationUseCase().first()

        assertEquals(2, result.size)

        val entity1 = result[0]
        assertEquals(1, entity1.id)
        assertEquals("John Doe", entity1.name)
        assertEquals("USA", entity1.country)
        assertEquals("john.jpg", entity1.image)
        assertEquals(40.7128, entity1.latitude, 0.0)
        assertEquals(-74.0060, entity1.longitude, 0.0)
        assertEquals("New York", entity1.prettyLocation)

        val entity2 = result[1]
        assertEquals(2, entity2.id)
        assertEquals("Jane Smith", entity2.name)
        assertEquals("Canada", entity2.country)
        assertNull(entity2.image)
        assertEquals(45.4215, entity2.latitude, 0.0)
        assertEquals(-75.6972, entity2.longitude, 0.0)
        assertEquals("Ottawa", entity2.prettyLocation)

        coVerify { locationResolver.getPrettyLocation(40.7128, -74.0060) }
        coVerify { locationResolver.getPrettyLocation(45.4215, -75.6972) }
    }

    @Test
    fun invokeHandlesLocationResolutionFailureGracefully() = runTest {
        val pupil = Pupil(
            pupilId = 1,
            name = "John Doe",
            country = "USA",
            image = null,
            latitude = 40.7128,
            longitude = -74.0060,
            remoteId = null,
            pendingSync = false,
            syncType = null
        )

        every { pupilRepository.pupils } returns flowOf(listOf(pupil))
        coEvery { locationResolver.getPrettyLocation(40.7128, -74.0060) } returns null

        val result = getPupilsWithLocationUseCase().first()

        assertEquals(1, result.size)
        val entity = result[0]
        assertEquals(1, entity.id)
        assertEquals("John Doe", entity.name)
        assertNull(entity.prettyLocation)
    }

    @Test
    fun invokeHandlesLocationResolverExceptionGracefully() = runTest {
        val pupil = Pupil(
            pupilId = 1,
            name = "John Doe",
            country = "USA",
            image = null,
            latitude = 40.7128,
            longitude = -74.0060,
            remoteId = null,
            pendingSync = false,
            syncType = null
        )

        every { pupilRepository.pupils } returns flowOf(listOf(pupil))
        coEvery { locationResolver.getPrettyLocation(40.7128, -74.0060) } throws RuntimeException("Network error")

        val result = getPupilsWithLocationUseCase().first()

        assertEquals(1, result.size)
        val entity = result[0]
        assertEquals(1, entity.id)
        assertEquals("John Doe", entity.name)
        assertNull(entity.prettyLocation)
    }

    @Test
    fun invokeMapsAllPupilFieldsCorrectly() = runTest {
        val pupil = Pupil(
            pupilId = 42,
            name = "Test User",
            country = "Test Country",
            image = "test.jpg",
            latitude = 51.5074,
            longitude = -0.1278,
            remoteId = 999,
            pendingSync = true,
            syncType = null
        )

        every { pupilRepository.pupils } returns flowOf(listOf(pupil))
        coEvery { locationResolver.getPrettyLocation(51.5074, -0.1278) } returns "London"

        val result = getPupilsWithLocationUseCase().first()

        assertEquals(1, result.size)
        val entity = result[0]
        assertEquals(42, entity.id)
        assertEquals("Test User", entity.name)
        assertEquals("Test Country", entity.country)
        assertEquals("test.jpg", entity.image)
        assertEquals(51.5074, entity.latitude, 0.0)
        assertEquals(-0.1278, entity.longitude, 0.0)
        assertEquals("London", entity.prettyLocation)
    }

    @Test
    fun invokeCallsLocationResolverForEachPupil() = runTest {
        val pupil1 = Pupil(
            pupilId = 1,
            name = "User 1",
            country = "Country 1",
            image = null,
            latitude = 10.0,
            longitude = 20.0,
            remoteId = null,
            pendingSync = false,
            syncType = null
        )
        val pupil2 = Pupil(
            pupilId = 2,
            name = "User 2",
            country = "Country 2",
            image = null,
            latitude = 30.0,
            longitude = 40.0,
            remoteId = null,
            pendingSync = false,
            syncType = null
        )

        every { pupilRepository.pupils } returns flowOf(listOf(pupil1, pupil2))
        coEvery { locationResolver.getPrettyLocation(10.0, 20.0) } returns "Location 1"
        coEvery { locationResolver.getPrettyLocation(30.0, 40.0) } returns "Location 2"

        getPupilsWithLocationUseCase().first()

        coVerify(exactly = 1) { locationResolver.getPrettyLocation(10.0, 20.0) }
        coVerify(exactly = 1) { locationResolver.getPrettyLocation(30.0, 40.0) }
    }
}
