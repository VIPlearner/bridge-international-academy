package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.domain.mapper.toDomainEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPupilsUseCase @Inject constructor(
    private val pupilRepository: IPupilRepository
) {
    operator fun invoke(): Flow<List<PupilEntity>> {
        return pupilRepository.pupils.map { pupils ->
            pupils.map { it.toDomainEntity() }
        }
    }
}
