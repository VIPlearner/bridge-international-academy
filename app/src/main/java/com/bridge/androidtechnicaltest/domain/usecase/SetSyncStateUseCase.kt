package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.datastore.DataStoreRepository
import com.bridge.androidtechnicaltest.domain.SyncState
import javax.inject.Inject

class SetSyncStateUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(syncState: SyncState) {
        dataStoreRepository.setPupilSyncState(syncState)
    }
}
