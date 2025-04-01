package com.example.customerlauncher.data.repostiory

import com.example.customerlauncher.data.dto.toWeatherInfo
import com.example.customerlauncher.data.remote.weather.WeatherDataSource
import com.example.customerlauncher.domain.repostority.WeatherRepository
import com.example.customerlauncher.domain.model.WeatherInfo

class WeatherRepositoryImpl(private val dataSource: WeatherDataSource) : WeatherRepository {
    override suspend fun getWeatherInfo(latitude: Double, longitude: Double): WeatherInfo {
        return dataSource.getWeatherInfo(latitude,longitude).toWeatherInfo()
    }
}