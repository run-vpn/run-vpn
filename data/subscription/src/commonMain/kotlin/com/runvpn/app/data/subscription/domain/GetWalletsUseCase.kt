package com.runvpn.app.data.subscription.domain

class GetWalletsUseCase(private val subscriptionRepository: SubscriptionRepository) {

    suspend operator fun invoke() = runCatching {
        return@runCatching subscriptionRepository.getWallets()
    }

}
