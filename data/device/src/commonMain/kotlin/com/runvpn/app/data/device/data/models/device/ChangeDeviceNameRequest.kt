package com.runvpn.app.data.device.data.models.device

import kotlinx.serialization.Serializable

@Serializable
data class ChangeDeviceNameRequest(
    val name: String
)
