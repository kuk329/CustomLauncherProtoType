package com.example.customerlauncher.data.remote.ipinfo

import com.example.customerlauncher.data.dto.IpinfoDTO
import retrofit2.http.GET

interface IpinfoApi {

    @GET("json")
    suspend fun getLocation(): IpinfoDTO
}