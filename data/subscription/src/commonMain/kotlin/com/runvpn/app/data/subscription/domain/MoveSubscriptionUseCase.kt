package com.runvpn.app.data.subscription.domain

class MoveSubscriptionUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {


    suspend operator fun invoke(subscriptionId: String, deviceUuid: String) {
        subscriptionRepository.unbindSubscription(
            subscriptionId = subscriptionId,
            deviceUuid = deviceUuid
        )
    }

}
