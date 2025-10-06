package com.bridge.androidtechnicaltest.data.network

import com.bridge.androidtechnicaltest.data.network.dto.CreatePupilRequest
import com.bridge.androidtechnicaltest.data.network.dto.PupilPageResponse
import com.bridge.androidtechnicaltest.data.network.dto.PupilResponse
import com.bridge.androidtechnicaltest.data.network.dto.UpdatePupilRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PupilApi {
    @GET("pupils")
    suspend fun getPupils(
        @Query("page") page: Int = 1,
    ): Response<PupilPageResponse>

    @GET("pupils/{pupilId}")
    suspend fun getPupil(
        @Path("pupilId") pupilId: Int,
    ): Response<PupilResponse>

    @POST("pupils")
    suspend fun createPupil(
        @Body pupilRequest: CreatePupilRequest,
    ): Response<PupilResponse>

    @PUT("pupils/{pupilId}")
    suspend fun updatePupil(
        @Path("pupilId") pupilId: Int,
        @Body pupilRequest: UpdatePupilRequest,
    ): Response<PupilResponse>

    @DELETE("pupils/{pupilId}")
    suspend fun deletePupil(
        @Path("pupilId") pupilId: Int,
    ): Response<Unit>
}
