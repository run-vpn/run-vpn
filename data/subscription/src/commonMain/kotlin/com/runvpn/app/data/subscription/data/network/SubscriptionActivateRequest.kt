package com.runvpn.app.data.subscription.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionActivateRequest(
    @SerialName("device_id")
    val deviceId: String
)
