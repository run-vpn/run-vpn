package com.runvpn.app.data.subscription.domain

import com.runvpn.app.data.subscription.data.network.Rate

/**
 * Returns minimum rate from all rates.
 */
class GetMinimumRateUseCase(
    private val getAvailableTariffsUseCase: GetAvailableTariffsUseCase
) {

    suspend operator fun invoke(): Rate {
        val allRates = getAvailableTariffsUseCase().getOrThrow()
        return allRates.minBy { it.periodInDays }
    }
}
