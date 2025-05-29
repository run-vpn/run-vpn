package com.runvpn.app.data.subscription.data.entity

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionDto(
    @SerialName("uuid")
    val id: String,
    val deviceUuid: String?,
    @SerialName("period")
    val periodInDays: Int,
    val expirationAt: Instant,
    val finishedAt: Instant?
)
