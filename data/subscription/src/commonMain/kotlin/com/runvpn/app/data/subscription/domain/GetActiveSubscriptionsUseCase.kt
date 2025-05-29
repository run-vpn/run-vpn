package com.runvpn.app.data.subscription.domain

class GetActiveSubscriptionsUseCase(
    private val subscriptionsRepository: SubscriptionRepository
) {

    suspend operator fun invoke() = runCatching {
        return@runCatching subscriptionsRepository.getUserSubscriptions()
    }
}
