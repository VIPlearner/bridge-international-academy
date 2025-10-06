package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.domain.mapper.toNewPupil
import com.bridge.androidtechnicaltest.utils.Result
import javax.inject.Inject

class AddPupilUseCase
    @Inject
    constructor(
        private val pupilRepository: IPupilRepository,
    ) {
        suspend operator fun invoke(pupil: PupilEntity): Result<Unit> =
            try {
                pupilRepository.addPupil(pupil.toNewPupil())
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error occurred while adding pupil")
            }
    }
