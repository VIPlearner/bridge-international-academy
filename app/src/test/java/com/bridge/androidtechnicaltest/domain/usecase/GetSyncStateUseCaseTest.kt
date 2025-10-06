package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.datastore.DataStoreRepository
import com.bridge.androidtechnicaltest.domain.SyncState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetSyncStateUseCaseTest {
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var getSyncStateUseCase: GetSyncStateUseCase

    @Before
    fun setup() {
        dataStoreRepository = mockk()
        getSyncStateUseCase = GetSyncStateUseCase(dataStoreRepository)
    }

    @Test
    fun `invoke returns OUT_OF_DATE when repository returns OUT_OF_DATE`() =
        runTest {
            every { dataStoreRepository.getPupilSyncState() } returns flowOf(SyncState.OUT_OF_DATE)

            val result = getSyncStateUseCase().first()

            assertEquals(SyncState.OUT_OF_DATE, result)
        }

    @Test
    fun `invoke returns UP_TO_DATE when repository returns UP_TO_DATE`() =
        runTest {
            every { dataStoreRepository.getPupilSyncState() } returns flowOf(SyncState.UP_TO_DATE)

            val result = getSyncStateUseCase().first()

            assertEquals(SyncState.UP_TO_DATE, result)
        }

    @Test
    fun `invoke returns SYNCING when repository returns SYNCING`() =
        runTest {
            every { dataStoreRepository.getPupilSyncState() } returns flowOf(SyncState.SYNCING)

            val result = getSyncStateUseCase().first()

            assertEquals(SyncState.SYNCING, result)
        }

    @Test
    fun `invoke returns flow that emits multiple values when repository flow changes`() =
        runTest {
            val flowValues = mutableListOf<SyncState>()
            every { dataStoreRepository.getPupilSyncState() } returns
                flowOf(
                    SyncState.OUT_OF_DATE,
                    SyncState.SYNCING,
                    SyncState.UP_TO_DATE,
                )

            getSyncStateUseCase().collect { state ->
                flowValues.add(state)
            }

            assertEquals(3, flowValues.size)
            assertEquals(SyncState.OUT_OF_DATE, flowValues[0])
            assertEquals(SyncState.SYNCING, flowValues[1])
            assertEquals(SyncState.UP_TO_DATE, flowValues[2])
        }
}
