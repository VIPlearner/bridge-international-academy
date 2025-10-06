package com.bridge.androidtechnicaltest.data.mapper

import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import com.bridge.androidtechnicaltest.data.network.dto.CreatePupilRequest
import com.bridge.androidtechnicaltest.data.network.dto.UpdatePupilRequest

/**
 * Mapper functions to convert between network DTOs and database entities
 */

fun Pupil.toCreatePupilRequest(): CreatePupilRequest =
    CreatePupilRequest(
        name = this.name,
        country = this.country,
        image = this.image,
        latitude = this.latitude,
        longitude = this.longitude,
    )

fun Pupil.toUpdatePupilRequest(): UpdatePupilRequest =
    UpdatePupilRequest(
        name = this.name,
        country = this.country,
        image = this.image,
        latitude = this.latitude,
        longitude = this.longitude,
    )
