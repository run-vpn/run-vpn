package com.runvpn.app.data.subscription.data.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class SubscriptionOrderRequest(
    @SerialName("rate_id")
    val rateId: Int,
    @SerialName("device_count")
    val deviceCount: Int,
    @SerialName("period")
    val period: Int,
    @SerialName("device_ids")
    val deviceIds: List<String>
)
