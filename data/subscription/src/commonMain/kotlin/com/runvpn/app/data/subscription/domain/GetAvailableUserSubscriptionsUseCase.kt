package com.runvpn.app.data.subscription.domain

class GetAvailableUserSubscriptionsUseCase(
    private val subscriptionsRepository: SubscriptionRepository
) {

    suspend operator fun invoke() = runCatching {
        return@runCatching subscriptionsRepository.getUserSubscriptions().filter {
            it.deviceUuid == null
        }
    }
}
