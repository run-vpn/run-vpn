package com.runvpn.app.data.device.data.models.auth

import kotlinx.serialization.Serializable

/** @param value can be [email address, phone number, telegram @username etc...] */
@Serializable
data class ConfirmAuthCodeRequest(
    val value: String,
    val code: String
)
