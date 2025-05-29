package com.runvpn.app.data.subscription.domain

import com.runvpn.app.data.subscription.data.network.Rate

class CalculateRateCostUseCase {

    operator fun invoke(rate: Rate, deviceCount: Int, periodInDays: Int): Double {
        val costInDollars: Long = rate.run {
            return@run (
                    deviceCount * (
                        deviceStartCoefficient +
                        deviceStepCoefficient * (deviceCount - deviceCountMin))
                    ).toLong()
        } / 100

        return costInDollars.toDouble()
    }
}
