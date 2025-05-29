package com.runvpn.app.data.subscription.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BuySubscriptionRequest(
    @SerialName("tariffUuid")
    val tariffUUID: String,
    val deviceCount: Int,
    @SerialName("deviceUuids")
    val deviceUUIDs: List<String>,
    val refill: Boolean
)
