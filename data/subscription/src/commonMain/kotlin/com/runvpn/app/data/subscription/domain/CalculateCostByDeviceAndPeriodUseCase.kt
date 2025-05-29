package com.runvpn.app.data.subscription.domain

import co.touchlab.kermit.Logger
import com.runvpn.app.data.subscription.data.network.Rate

class CalculateCostByDeviceAndPeriodUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {
    companion object {
        private val logger = Logger.withTag("CalculateCostByDeviceAndPeriodUseCase")
    }
    suspend operator fun invoke(deviceCount: Int, period: Int): CalculateCostResult {
        val rates = subscriptionRepository.getSubscriptionRates()

        val selectedRate = rates.find {
            deviceCount >= it.deviceCountMin && deviceCount <= it.deviceCountMax &&
                    period == it.periodInDays && it.isActive
        } ?: return CalculateCostResult.RateNotFound

        val costInDollars: Long = selectedRate.run {
            val calculationResult = (
                    deviceCount * (deviceStartCoefficient + deviceStepCoefficient * (deviceCount - deviceCountMin)))
            return@run calculationResult.toLong()
        } / 100

        return CalculateCostResult.Success(costInDollars, selectedRate)
    }
}

sealed interface CalculateCostResult {
    data class Success(val cost: Long, val rate: Rate) : CalculateCostResult
    data object RateNotFound : CalculateCostResult
}
