package com.runvpn.app.data.device.data.models.device.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val token: String,
    val deviceUuid: String
)

