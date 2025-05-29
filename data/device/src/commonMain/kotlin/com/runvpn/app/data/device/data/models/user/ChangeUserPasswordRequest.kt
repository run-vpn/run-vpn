package com.runvpn.app.data.device.data.models.user

import kotlinx.serialization.Serializable

@Serializable
data class ChangeUserPasswordRequest(
    val password: String
)
