package com.example.customerlauncher.data.remote.weather

import com.example.customerlauncher.common.Constants
import com.example.customerlauncher.data.dto.WeatherDTO

class WeatherRemoteDatasource(private val api: WeatherAPI): WeatherDataSource {
    override suspend fun getWeatherInfo(latitude:Double, longitude:Double): WeatherDTO {
        return api.getWeather(latitude,longitude, Constants.API_KEY)
    }
}