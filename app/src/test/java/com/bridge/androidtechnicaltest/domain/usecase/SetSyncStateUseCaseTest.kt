package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.datastore.DataStoreRepository
import com.bridge.androidtechnicaltest.domain.SyncState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SetSyncStateUseCaseTest {
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var setSyncStateUseCase: SetSyncStateUseCase

    @Before
    fun setup() {
        dataStoreRepository = mockk()
        setSyncStateUseCase = SetSyncStateUseCase(dataStoreRepository)
    }

    @Test
    fun `invoke calls repository setPupilSyncState with OUT_OF_DATE`() =
        runTest {
            coEvery { dataStoreRepository.setPupilSyncState(any()) } returns Unit

            setSyncStateUseCase(SyncState.OUT_OF_DATE)

            coVerify { dataStoreRepository.setPupilSyncState(SyncState.OUT_OF_DATE) }
        }

    @Test
    fun `invoke calls repository setPupilSyncState with UP_TO_DATE`() =
        runTest {
            coEvery { dataStoreRepository.setPupilSyncState(any()) } returns Unit

            setSyncStateUseCase(SyncState.UP_TO_DATE)

            coVerify { dataStoreRepository.setPupilSyncState(SyncState.UP_TO_DATE) }
        }

    @Test
    fun `invoke calls repository setPupilSyncState with SYNCING`() =
        runTest {
            coEvery { dataStoreRepository.setPupilSyncState(any()) } returns Unit

            setSyncStateUseCase(SyncState.SYNCING)

            coVerify { dataStoreRepository.setPupilSyncState(SyncState.SYNCING) }
        }

    @Test
    fun `invoke calls repository exactly once`() =
        runTest {
            coEvery { dataStoreRepository.setPupilSyncState(any()) } returns Unit

            setSyncStateUseCase(SyncState.SYNCING)

            coVerify(exactly = 1) { dataStoreRepository.setPupilSyncState(any()) }
        }
}
