package com.example.uvbuddy.presentation.repositories

import com.example.uvbuddy.presentation.data.ThingSpeakService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UVRepository {
    private val service: ThingSpeakService

    init {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        service = Retrofit.Builder()
            .baseUrl(ThingSpeakService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ThingSpeakService::class.java)
    }

    suspend fun fetchLatestUV(): Float? {
        val channelId = "2876817"
        val apiKey = "JGSCB36IGZ5QLOJV"
        val response = service.getLatestFeed(channelId, apiKey)
        val uvString = response.feeds.firstOrNull()?.uvIndex
        return uvString?.toFloatOrNull()
    }
}