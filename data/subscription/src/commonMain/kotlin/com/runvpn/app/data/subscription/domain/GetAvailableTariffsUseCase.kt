package com.runvpn.app.data.subscription.domain


class GetAvailableTariffsUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {

    suspend operator fun invoke() = runCatching {
        return@runCatching subscriptionRepository.getSubscriptionRates().filter { it.isActive }
    }
}
