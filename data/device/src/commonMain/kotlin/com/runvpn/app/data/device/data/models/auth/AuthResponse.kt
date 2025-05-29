package com.runvpn.app.data.device.data.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val isNewUser: Boolean?
)
