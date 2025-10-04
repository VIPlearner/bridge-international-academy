package com.bridge.androidtechnicaltest.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Network response model for reverse geocoding
 */
data class GeocodingResponse(
    @SerializedName("name")
    val name: String,

    @SerializedName("local_names")
    val localNames: Map<String, String>?,

    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lon")
    val longitude: Double,

    @SerializedName("country")
    val country: String,

    @SerializedName("state")
    val state: String?
)
