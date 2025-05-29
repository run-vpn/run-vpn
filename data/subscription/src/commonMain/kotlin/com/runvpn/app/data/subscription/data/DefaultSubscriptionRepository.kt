package com.runvpn.app.data.subscription.data

import com.runvpn.app.data.subscription.data.entity.SubscriptionDto
import com.runvpn.app.data.subscription.data.entity.toDomain
import com.runvpn.app.data.subscription.data.network.BuySubscriptionRequest
import com.runvpn.app.data.subscription.data.network.GetWalletsResponse
import com.runvpn.app.data.subscription.data.network.Rate
import com.runvpn.app.data.subscription.data.network.SubscriptionActivateRequest
import com.runvpn.app.data.subscription.data.network.SubscriptionActivateResponse
import com.runvpn.app.data.subscription.data.network.SubscriptionOrderRequest
import com.runvpn.app.data.subscription.data.network.SubscriptionsApi
import com.runvpn.app.data.subscription.domain.SubscriptionRepository
import com.runvpn.app.data.subscription.domain.entities.Subscription
import com.runvpn.app.data.subscription.domain.entities.SubscriptionsCount
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost

internal class DefaultSubscriptionRepository(
    private val subscriptionsApi: SubscriptionsApi
) : SubscriptionRepository {

    private var cachedRates: List<Rate> = listOf()

    override suspend fun getSubscriptionRates(): List<Rate> {
        if (cachedRates.isNotEmpty()) return cachedRates

        val rates = subscriptionsApi.getSubscriptionRates()
        cachedRates = rates.getOrThrow().items
        return cachedRates
    }

    override suspend fun sendSubscriptionOrder(
        order: SubscriptionOrderRequest,
        deviceAppUuid: String
    ): GetWalletsResponse {
        return subscriptionsApi.sendSubscriptionOrder(order)
    }

    override suspend fun subscriptionActivate(
        subscriptionId: String,
        device: SubscriptionActivateRequest
    ): SubscriptionActivateResponse = subscriptionsApi.subscriptionActivate(subscriptionId, device)

    override suspend fun activateSubscription(
        uuid: String, deviceUuid: String
    ): List<Subscription> {
        return subscriptionsApi.activateSubscription(uuid, deviceUuid).getOrThrow().map {
            it.toDomain()
        }
    }

    override suspend fun unbindSubscription(subscriptionId: String, deviceUuid: String) {
        //todo сделать запрос на api
    }

    override suspend fun getUserSubscriptions(): List<Subscription> {
        return subscriptionsApi.getUserSubscriptions().getOrThrow().map { it.toDomain() }
    }

    override suspend fun buySubscription(
        tariffUUID: String,
        deviceCount: Int,
        deviceUUIDs: List<String>,
        refill: Boolean
    ): List<Subscription> {
        return subscriptionsApi.buySubscription(
            buySubscriptionRequest = BuySubscriptionRequest(
                tariffUUID = tariffUUID,
                deviceCount = deviceCount,
                deviceUUIDs = deviceUUIDs,
                refill = refill
            )
        ).getOrThrow().map { it.toDomain() }
    }

    override suspend fun getWallets(): List<WalletWithRateCost> {
        return subscriptionsApi.getWallets().getOrThrow().wallets.map {
            WalletWithRateCost(
                blockchain = it.blockchain,
                address = it.address,
                currency = it.currency,
                rate = it.rate
            )
        }
    }

    override suspend fun getSubscriptionsCount(): SubscriptionsCount {
        return subscriptionsApi.getSubscriptionsCount().getOrThrow().toDomain()
    }

    private fun SubscriptionDto.toDomain(): Subscription {
        return Subscription(
            id = this.id,
            deviceUuid = this.deviceUuid,
            periodInDays = this.periodInDays,
            expirationAt = this.expirationAt,
            finishedAt = this.finishedAt
        )
    }
}
