package com.runvpn.app.data.subscription.domain

class ActivateSubscriptionUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {

    suspend operator fun invoke(
        subscriptionId: String,
        deviceId: String
    ) = runCatching {
        val result = subscriptionRepository.activateSubscription(
            uuid = subscriptionId,
            deviceUuid = deviceId
        )
        return@runCatching result.first { it.id == subscriptionId }
    }
}
