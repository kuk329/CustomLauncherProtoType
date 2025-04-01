package com.example.customerlauncher.data.dto

import com.example.customerlauncher.domain.model.IpInfo
import com.google.gson.annotations.SerializedName

data class IpinfoDTO(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("ip")
    val ip: String,
    @SerializedName("loc")
    val loc: String,
    @SerializedName("org")
    val org: String,
    @SerializedName("postal")
    val postal: String,
    @SerializedName("readme")
    val readme: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("timezone")
    val timezone: String
)

fun IpinfoDTO.toIpinfo(): IpInfo {
    return IpInfo(city, ip, loc)
}