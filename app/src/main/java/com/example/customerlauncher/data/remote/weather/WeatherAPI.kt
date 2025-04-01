package com.example.customerlauncher.data.remote.weather

import com.example.customerlauncher.data.dto.WeatherDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang:String = "kr"
    ): WeatherDTO
}