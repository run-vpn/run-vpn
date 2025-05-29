package com.runvpn.app.data.subscription.domain.entities

import com.runvpn.app.data.subscription.data.network.Rate
import kotlinx.serialization.Serializable

@Serializable
data class ChosenSubscription(
    val rate: Rate,
    val deviceCount: Int,
    val periodInDays: Int
)
