package com.bridge.androidtechnicaltest.domain.entity

data class PupilWithLocationEntity(
    val id: Int = -1,
    val name: String,
    val country: String,
    val image: String?,
    val latitude: Double,
    val longitude: Double,
    val prettyLocation: String? = null,
) {
    companion object {
        fun fromPupilEntityAndPrettyLocation(
            pupil: PupilEntity,
            prettyLocation: String?,
        ): PupilWithLocationEntity =
            PupilWithLocationEntity(
                id = pupil.id,
                name = pupil.name,
                country = pupil.country,
                image = pupil.image,
                latitude = pupil.latitude,
                longitude = pupil.longitude,
                prettyLocation = prettyLocation,
            )
    }
}
