package com.bridge.androidtechnicaltest.domain.usecase

import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.data.repository.LocationResolver
import com.bridge.androidtechnicaltest.domain.entity.PupilWithLocationEntity
import com.bridge.androidtechnicaltest.domain.mapper.toDomainEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class GetPupilsWithLocationUseCase
    @Inject
    constructor(
        private val pupilRepository: IPupilRepository,
        private val locationResolver: LocationResolver,
    ) {
        operator fun invoke(): Flow<List<PupilWithLocationEntity>> =
            pupilRepository.pupils.map { pupils ->
                pupils.map { pupil ->
                    val baseEntity = pupil.toDomainEntity()
                    val prettyLocation =
                        try {
                            locationResolver.getPrettyLocation(pupil.latitude, pupil.longitude)
                        } catch (e: Exception) {
                            Timber.e(e, "Error resolving location for pupil id=${pupil.pupilId}")
                            null
                        }

                    PupilWithLocationEntity(
                        id = baseEntity.id,
                        name = baseEntity.name,
                        country = baseEntity.country,
                        image = baseEntity.image,
                        latitude = baseEntity.latitude,
                        longitude = baseEntity.longitude,
                        prettyLocation = prettyLocation,
                    )
                }
            }
    }
