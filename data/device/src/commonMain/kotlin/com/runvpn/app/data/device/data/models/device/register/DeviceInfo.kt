package com.runvpn.app.data.device.data.models.device.register

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val guid: String,
    val timezone: String,
    val language: String,
    val pushToken: String,
    val source: String,
    val referrer: String?,
    val hardware: Hardware,
    val software: Software,
    val application: ApplicationInfo,
//    @SerialName("application_code")
//    val applicationCode:String,
//    @SerialName("application_source")
//    val applicationSource: String,
    val customData: CustomData?,
    val applicationPackageName: String
)
