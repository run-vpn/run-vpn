package com.runvpn.app.data.subscription.domain.entities

data class SubscriptionsCount(
    val total: Int,
    val activated: Int,
    val availableToActivate: Int
) {
    companion object {
        val DEFAULT = SubscriptionsCount(
            total = 0,
            activated = 0,
            availableToActivate = 0
        )
    }
}


