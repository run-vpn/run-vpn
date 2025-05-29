package com.runvpn.app.data.device.data.models.device.register

import kotlinx.serialization.Serializable

@Serializable
data class Software(
    val platformName: String,
    val versionName: String,
    val versionCode: String,
    val name: String
)
