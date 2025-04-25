package com.example.uvbuddy.presentation.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ThingSpeakService {
    @GET("channels/{channelId}/feeds.json")
    suspend fun getLatestFeed(
        @Path("channelId") channelId: String,
        @Query("api_key") apiKey: String,
        @Query("results") results: Int = 1
    ): ThingSpeakResponse

    companion object {
        const val BASE_URL = "https://api.thingspeak.com/"
    }
}