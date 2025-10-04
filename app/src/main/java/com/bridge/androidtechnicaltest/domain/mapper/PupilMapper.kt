package com.bridge.androidtechnicaltest.domain.mapper

import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity

fun Pupil.toDomainEntity(): PupilEntity {
    return PupilEntity(
        id = pupilId,
        name = name,
        country = country,
        image = image,
        latitude = latitude,
        longitude = longitude
    )
}

fun PupilEntity.toNewPupil(): Pupil {
    return Pupil(
        pupilId = if (id == -1) -1 else id,
        name = name,
        country = country,
        image = image,
        latitude = latitude,
        longitude = longitude,
        remoteId = null,
        pendingSync = false,
        syncType = null
    )
}

fun PupilEntity.toUpdatedPupil(existingPupil: Pupil): Pupil {
    return existingPupil.copy(
        name = name,
        country = country,
        image = image,
        latitude = latitude,
        longitude = longitude
    )
}
