package com.runvpn.app.data.subscription.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionActivateResponse(
    val id: String,
    @SerialName("finished_at")
    val finishedAt: Long
)
