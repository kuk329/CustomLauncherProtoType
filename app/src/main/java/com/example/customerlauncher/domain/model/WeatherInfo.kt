package com.example.customerlauncher.domain.model

data class WeatherInfo(
    val temp : String,
    val city: String,
    val humidity: String,
    val weather: String,
    val weatherIcon: String,
    val weatherId: Int,
    val windSpeed: Double,
)
