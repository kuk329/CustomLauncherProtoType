package com.example.customerlauncher.data.remote.weather

import com.example.customerlauncher.data.dto.WeatherDTO

interface WeatherDataSource {
    suspend fun getWeatherInfo(latitude:Double, longitude:Double): WeatherDTO
}