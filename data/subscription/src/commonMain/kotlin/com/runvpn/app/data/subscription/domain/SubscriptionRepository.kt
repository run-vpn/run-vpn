package com.runvpn.app.data.subscription.domain

import com.runvpn.app.data.subscription.data.network.GetWalletsResponse
import com.runvpn.app.data.subscription.data.network.Rate
import com.runvpn.app.data.subscription.data.network.SubscriptionActivateRequest
import com.runvpn.app.data.subscription.data.network.SubscriptionActivateResponse
import com.runvpn.app.data.subscription.data.network.SubscriptionOrderRequest
import com.runvpn.app.data.subscription.domain.entities.Subscription
import com.runvpn.app.data.subscription.domain.entities.SubscriptionsCount
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost

interface SubscriptionRepository {

    suspend fun getSubscriptionRates(): List<Rate>

    suspend fun sendSubscriptionOrder(
        order: SubscriptionOrderRequest,
        deviceAppUuid: String
    ): GetWalletsResponse

    suspend fun subscriptionActivate(
        subscriptionId: String,
        device: SubscriptionActivateRequest
    ): SubscriptionActivateResponse

    suspend fun activateSubscription(
        uuid: String,
        deviceUuid: String
    ): List<Subscription>

    suspend fun unbindSubscription(subscriptionId: String, deviceUuid: String)

    suspend fun getUserSubscriptions(): List<Subscription>

    suspend fun buySubscription(
        tariffUUID: String,
        deviceCount: Int,
        deviceUUIDs: List<String>,
        refill: Boolean
    ): List<Subscription>

    suspend fun getWallets(): List<WalletWithRateCost>

    suspend fun getSubscriptionsCount(): SubscriptionsCount

}
