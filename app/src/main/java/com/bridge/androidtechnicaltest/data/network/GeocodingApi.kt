package com.bridge.androidtechnicaltest.data.network

import com.bridge.androidtechnicaltest.config.ApiConstants
import com.bridge.androidtechnicaltest.data.network.dto.GeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    /**
     * Reverse geocoding to get location name by geographical coordinates.
     *
     * @param latitude Geographical coordinate (latitude)
     * @param longitude Geographical coordinate (longitude)
     * @param limit Number of location names in the API response (optional)
     * @param apiKey Your unique API key
     */
    @GET("geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int? = null,
        @Query("appid") apiKey: String = ApiConstants.GEOCODING_API_KEY,
    ): Response<List<GeocodingResponse>>
}
