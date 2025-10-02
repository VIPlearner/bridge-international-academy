package com.bridge.androidtechnicaltest.data.network

import com.bridge.androidtechnicaltest.data.network.dto.CreatePupilRequest
import com.bridge.androidtechnicaltest.data.network.dto.PupilPageResponse
import com.bridge.androidtechnicaltest.data.network.dto.PupilResponse
import com.bridge.androidtechnicaltest.data.network.dto.UpdatePupilRequest
import retrofit2.Response
import retrofit2.http.*

interface PupilApi {

    /**
     * Gets a paged collection of pupils. Each page has five pupils.
     */
    @GET("pupils")
    suspend fun getPupils(
        @Query("page") page: Int = 1
    ): Response<PupilPageResponse>

    /**
     * Gets the pupil with the specified pupil ID.
     */
    @GET("pupils/{pupilId}")
    suspend fun getPupil(
        @Path("pupilId") pupilId: Int
    ): Response<PupilResponse>

    /**
     * Creates a new pupil.
     */
    @POST("pupils")
    suspend fun createPupil(
        @Body pupilRequest: CreatePupilRequest
    ): Response<PupilResponse>

    /**
     * Updates the pupil with the specified pupil ID.
     */
    @PUT("pupils/{pupilId}")
    suspend fun updatePupil(
        @Path("pupilId") pupilId: Int,
        @Body pupilRequest: UpdatePupilRequest
    ): Response<PupilResponse>

    /**
     * Deletes the pupil with the specified pupil ID.
     */
    @DELETE("pupils/{pupilId}")
    suspend fun deletePupil(
        @Path("pupilId") pupilId: Int
    ): Response<Unit>
}