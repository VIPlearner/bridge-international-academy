package com.bridge.androidtechnicaltest.domain.entity

data class PupilEntity(
    val id: Int = -1,
    val name: String,
    val country: String,
    val image: String?,
    val latitude: Double,
    val longitude: Double
)
