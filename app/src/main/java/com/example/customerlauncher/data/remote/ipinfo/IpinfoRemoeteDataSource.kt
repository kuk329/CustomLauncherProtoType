package com.example.customerlauncher.data.remote.ipinfo

import com.example.customerlauncher.data.dto.IpinfoDTO


class IpinfoRemoeteDataSource(private val api: IpinfoApi): IpinfoDataSource {
    override suspend fun getIpInfo(): IpinfoDTO {
        return api.getLocation()
    }
}