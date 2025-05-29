package com.runvpn.app.data.device.data.models.device

import kotlinx.serialization.Serializable

@Serializable
data class DeviceApplication(
    val code: String,
    val source: String,
    val versionName: String
)
