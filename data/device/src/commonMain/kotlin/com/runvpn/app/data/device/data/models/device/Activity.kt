package com.runvpn.app.data.device.data.models.device

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    val ip: String,
    @SerialName("createdAt")
    val createdAt: Instant,
)
