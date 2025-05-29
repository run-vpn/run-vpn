package com.runvpn.app.data.subscription.domain

import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.subscription.data.network.SubscriptionOrderRequest
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SendOrderAndGetWalletsUseCase(
    private val subscriptionRepository: SubscriptionRepository,
    private val appSettingsRepository: AppSettingsRepository
) {
    suspend operator fun invoke(
        rateId: Int,
        cost: Long,
        deviceCount: Int,
        period: Int
    ): List<WalletWithRateCost> =
        withContext(Dispatchers.IO) {
            val deviceUuid = appSettingsRepository.deviceUuid

            checkNotNull(deviceUuid) { "appUUID must not be null" }

            val remoteWallets = subscriptionRepository.sendSubscriptionOrder(
                order = SubscriptionOrderRequest(
                    rateId = rateId,
                    deviceCount = deviceCount,
                    period = period,
                    deviceIds = listOf(deviceUuid)
                ),
                deviceAppUuid = deviceUuid
            ).wallets

            return@withContext remoteWallets.map {
                WalletWithRateCost(
                    blockchain = it.blockchain,
                    currency = it.currency,
                    address = it.address,
                    rate = (cost / it.rate.toDouble()).toString()
                )
            }
        }
}
