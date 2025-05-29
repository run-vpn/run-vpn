package com.runvpn.app.data.subscription.domain


class BuySubscriptionUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {

    suspend operator fun invoke(
        tariffUUID: String,
        deviceCount: Int,
        deviceUuids: List<String>
    ) = runCatching {
        subscriptionRepository.buySubscription(
            tariffUUID = tariffUUID,
            deviceCount = deviceCount,
            deviceUUIDs = deviceUuids,
            refill = true
        )
    }
}
