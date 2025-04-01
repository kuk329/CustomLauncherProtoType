package com.example.customerlauncher.domain.repostority

import com.example.customerlauncher.domain.model.IpInfo


interface IpinfoRepository {
    suspend fun getIpInfo(): IpInfo
}