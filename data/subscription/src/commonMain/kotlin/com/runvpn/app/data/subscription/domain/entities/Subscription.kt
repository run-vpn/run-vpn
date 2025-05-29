package com.runvpn.app.data.subscription.domain.entities

import kotlinx.datetime.Instant

data class Subscription(
    val id: String,
    val deviceUuid: String?,
    val periodInDays: Int,
    val expirationAt: Instant,
    val finishedAt: Instant?
)
