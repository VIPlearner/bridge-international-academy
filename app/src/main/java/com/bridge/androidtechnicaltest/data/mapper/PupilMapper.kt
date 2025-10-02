package com.bridge.androidtechnicaltest.data.mapper

import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.db.dto.PupilList
import com.bridge.androidtechnicaltest.data.network.dto.CreatePupilRequest
import com.bridge.androidtechnicaltest.data.network.dto.PupilPageResponse
import com.bridge.androidtechnicaltest.data.network.dto.PupilResponse
import com.bridge.androidtechnicaltest.data.network.dto.UpdatePupilRequest

/**
 * Mapper functions to convert between network DTOs and database entities
 */

// Convert network PupilResponse to database Pupil entity
fun PupilResponse.toDbPupil(): Pupil {
    return Pupil(
        pupilId = this.pupilId,
        name = this.name,
        country = this.country,
        image = this.image,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

// Convert database Pupil entity to network PupilResponse
fun Pupil.toPupilResponse(): PupilResponse {
    return PupilResponse(
        pupilId = this.pupilId,
        name = this.name,
        country = this.country,
        image = this.image,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

// Convert network PupilPageResponse to database PupilList
fun PupilPageResponse.toPupilList(): PupilList {
    return PupilList(
        items = this.items.map { it.toDbPupil() }.toMutableList()
    )
}

// Convert database Pupil to CreatePupilRequest
fun Pupil.toCreatePupilRequest(): CreatePupilRequest {
    return CreatePupilRequest(
        name = this.name,
        country = this.country,
        image = this.image,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

// Convert database Pupil to UpdatePupilRequest
fun Pupil.toUpdatePupilRequest(): UpdatePupilRequest {
    return UpdatePupilRequest(
        name = this.name,
        country = this.country,
        image = this.image,
        latitude = this.latitude,
        longitude = this.longitude
    )
}
