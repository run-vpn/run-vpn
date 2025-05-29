package com.runvpn.app.data.device.data.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthByPasswordRequest(
    val value: String,
    val user: UserBody
)

@Serializable
data class UserBody(
    val password: String
)
