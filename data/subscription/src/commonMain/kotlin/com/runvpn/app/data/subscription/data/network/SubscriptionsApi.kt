package com.runvpn.app.data.subscription.data.network

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.subscription.data.entity.SubscriptionDto
import com.runvpn.app.data.subscription.data.entity.SubscriptionsCountDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface SubscriptionsApi {

    @GET("api/tariffs")
    suspend fun getSubscriptionRates(): ApiResponse<RatesResponse>

    @POST("api/expected_orders")
    suspend fun sendSubscriptionOrder(
        @Body order: SubscriptionOrderRequest,
    ): GetWalletsResponse

    @POST("api/rates_orders_rights/{id}/subscriptions")
    suspend fun subscriptionActivate(
        @Path("id") subscriptionId: String,
        @Body body: SubscriptionActivateRequest
    ): SubscriptionActivateResponse

    @POST("api/subscriptions/{uuid}/activate/{deviceUuid}")
    suspend fun activateSubscription(
        @Path("uuid") uuid: String,
        @Path("deviceUuid") deviceUuid: String
    ): ApiResponse<List<SubscriptionDto>>

    @GET("api/subscriptions")
    suspend fun getUserSubscriptions(): ApiResponse<List<SubscriptionDto>>

    @POST("api/subscriptions")
    suspend fun buySubscription(
        @Body buySubscriptionRequest: BuySubscriptionRequest
    ): ApiResponse<List<SubscriptionDto>>

    @GET("api/payments/wallets")
    suspend fun getWallets(): ApiResponse<GetWalletsResponse>

    @GET("api/subscriptions/counts")
    suspend fun getSubscriptionsCount(): ApiResponse<SubscriptionsCountDto>
}
