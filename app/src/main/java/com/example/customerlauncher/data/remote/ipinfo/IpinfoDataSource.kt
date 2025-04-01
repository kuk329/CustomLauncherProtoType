package com.example.customerlauncher.data.remote.ipinfo

import com.example.customerlauncher.data.dto.IpinfoDTO


interface IpinfoDataSource {
    suspend fun getIpInfo(): IpinfoDTO
}