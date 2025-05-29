package com.runvpn.app.data.subscription.domain

class GetSubscriptionsCountUseCase(private val subscriptionsRepository: SubscriptionRepository) {

    suspend operator fun invoke() = runCatching {
        return@runCatching subscriptionsRepository.getSubscriptionsCount()
    }

}
