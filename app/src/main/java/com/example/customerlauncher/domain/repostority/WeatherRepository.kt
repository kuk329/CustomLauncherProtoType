package com.example.customerlauncher.domain.repostority

import com.example.customerlauncher.domain.model.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherInfo(latitude:Double, longitude: Double):WeatherInfo
}