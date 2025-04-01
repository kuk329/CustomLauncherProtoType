package com.example.customerlauncher.data.repostiory

import com.example.customerlauncher.data.dto.toIpinfo
import com.example.customerlauncher.data.remote.ipinfo.IpinfoDataSource
import com.example.customerlauncher.domain.model.IpInfo
import com.example.customerlauncher.domain.repostority.IpinfoRepository


class IpinfoRepostiroyImpl(private val dataSource: IpinfoDataSource): IpinfoRepository {
    override suspend fun getIpInfo(): IpInfo {
        return dataSource.getIpInfo().toIpinfo()
    }
}