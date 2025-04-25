package com.example.uvbuddy.presentation.data

import com.google.gson.annotations.SerializedName

data class ThingSpeakResponse(
    @SerializedName("feeds") val feeds: List<Feed>
)
data class Feed(
    @SerializedName("field1") val uvIndex: String?
)