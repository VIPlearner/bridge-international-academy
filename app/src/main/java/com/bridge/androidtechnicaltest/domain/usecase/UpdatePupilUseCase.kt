package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.domain.mapper.toUpdatedPupil
import com.bridge.androidtechnicaltest.utils.Result
import javax.inject.Inject

class UpdatePupilUseCase @Inject constructor(
    private val pupilRepository: IPupilRepository
) {
    suspend operator fun invoke(pupil: PupilEntity): Result<Unit> {
        return try {
            val existingPupil = pupilRepository.getPupilById(pupil.id)
            if (existingPupil == null) {
                Result.Error("Pupil with ID ${pupil.id} not found")
            } else {
                val updatedPupil = pupil.toUpdatedPupil(existingPupil)
                pupilRepository.updatePupil(updatedPupil)
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error occurred while updating pupil")
        }
    }
}
