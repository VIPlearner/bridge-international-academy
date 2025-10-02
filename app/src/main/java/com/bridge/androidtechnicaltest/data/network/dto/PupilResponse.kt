package com.bridge.androidtechnicaltest.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Network response model for a single pupil
 */
data class PupilResponse(
    @SerializedName("pupilId")
    val pupilId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("image")
    val image: String?,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)

/**
 * Network response model for paginated pupils list
 */
data class PupilPageResponse(
    @SerializedName("items")
    val items: List<PupilResponse>,

    @SerializedName("pageNumber")
    val pageNumber: Int,

    @SerializedName("itemCount")
    val itemCount: Int,

    @SerializedName("totalPages")
    val totalPages: Int
)

/**
 * Network request model for creating a new pupil
 */
data class CreatePupilRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("image")
    val image: String?,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)

/**
 * Network request model for updating a pupil
 */
data class UpdatePupilRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("image")
    val image: String?,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)

/**
 * Network error response model
 */
data class ProblemDetails(
    @SerializedName("type")
    val type: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("status")
    val status: Int?,

    @SerializedName("detail")
    val detail: String?,

    @SerializedName("instance")
    val instance: String?
)
