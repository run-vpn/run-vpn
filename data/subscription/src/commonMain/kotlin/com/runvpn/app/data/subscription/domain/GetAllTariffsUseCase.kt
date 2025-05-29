package com.runvpn.app.data.subscription.domain

import com.runvpn.app.data.subscription.data.network.Rate

class GetAllTariffsUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {

    suspend operator fun invoke(): List<Rate> {
        return subscriptionRepository.getSubscriptionRates()
    }
}
