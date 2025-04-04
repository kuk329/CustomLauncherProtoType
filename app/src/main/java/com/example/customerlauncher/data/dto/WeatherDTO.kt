package com.example.customerlauncher.data.dto


import com.example.apisample.data.dto.*
import com.example.customerlauncher.domain.model.WeatherInfo
import com.google.gson.annotations.SerializedName

data class WeatherDTO(
    @SerializedName("base")
    val base: String,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("cod")
    val cod: Int,
    @SerializedName("coord")
    val coord: Coord,
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: Main,
    @SerializedName("name")
    val name: String,
    @SerializedName("sys")
    val sys: Sys,
    @SerializedName("timezone")
    val timezone: Int,
    @SerializedName("visibility")
    val visibility: Int,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind")
    val wind: Wind
)

fun WeatherDTO.toWeatherInfo(): WeatherInfo {
    val description = weather.firstOrNull()?.description ?: "Unknown"
    val icon = weather.firstOrNull()?.icon ?:"Unknown"
    val id = weather.firstOrNull()?.id?: 800
    return WeatherInfo(main.temp.toString(), name, main.humidity.toString(), description, icon, id, wind.speed)
}