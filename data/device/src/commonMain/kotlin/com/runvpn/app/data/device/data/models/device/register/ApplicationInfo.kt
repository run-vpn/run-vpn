package com.runvpn.app.data.device.data.models.device.register

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationInfo(
    val code: String,
    val source: String
)
