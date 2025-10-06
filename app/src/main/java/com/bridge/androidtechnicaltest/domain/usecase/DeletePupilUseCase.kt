package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.utils.Result
import javax.inject.Inject

class DeletePupilUseCase
    @Inject
    constructor(
        private val pupilRepository: IPupilRepository,
    ) {
        suspend operator fun invoke(pupilId: Int): Result<Unit> =
            try {
                pupilRepository.deletePupil(pupilId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error occurred while deleting pupil")
            }
    }
