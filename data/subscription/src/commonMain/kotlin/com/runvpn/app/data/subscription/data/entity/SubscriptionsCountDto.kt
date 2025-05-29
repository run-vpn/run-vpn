package com.runvpn.app.data.subscription.data.entity

import com.runvpn.app.data.subscription.domain.entities.SubscriptionsCount
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionsCountDto(
    val total: Int,
    val activated: Int,
) {
    val availableToActivate = total - activated
}


fun SubscriptionsCountDto.toDomain() = SubscriptionsCount(
    total = total,
    activated = activated,
    availableToActivate = availableToActivate
)
