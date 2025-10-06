package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.datastore.DataStoreRepository
import com.bridge.androidtechnicaltest.domain.SyncState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSyncStateUseCase
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
    ) {
        operator fun invoke(): Flow<SyncState> = dataStoreRepository.getPupilSyncState()
    }
