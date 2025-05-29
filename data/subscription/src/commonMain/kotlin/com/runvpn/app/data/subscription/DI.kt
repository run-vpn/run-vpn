package com.runvpn.app.data.subscription

import com.runvpn.app.core.network.NetworkApiFactory
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.subscription.data.DefaultSubscriptionRepository
import com.runvpn.app.data.subscription.data.network.SubscriptionsApi
import com.runvpn.app.data.subscription.domain.ActivateSubscriptionUseCase
import com.runvpn.app.data.subscription.domain.BuySubscriptionUseCase
import com.runvpn.app.data.subscription.domain.CalculateCostByDeviceAndPeriodUseCase
import com.runvpn.app.data.subscription.domain.CalculateRateCostUseCase
import com.runvpn.app.data.subscription.domain.GetActiveSubscriptionsUseCase
import com.runvpn.app.data.subscription.domain.GetAvailableTariffsUseCase
import com.runvpn.app.data.subscription.domain.GetAvailableUserSubscriptionsUseCase
import com.runvpn.app.data.subscription.domain.GetDevicesWithTariffUseCase
import com.runvpn.app.data.subscription.domain.GetMinimumRateUseCase
import com.runvpn.app.data.subscription.domain.GetAllTariffsUseCase
import com.runvpn.app.data.subscription.domain.GetSubscriptionsCountUseCase
import com.runvpn.app.data.subscription.domain.GetWalletsUseCase
import com.runvpn.app.data.subscription.domain.MoveSubscriptionUseCase
import com.runvpn.app.data.subscription.domain.SendOrderAndGetWalletsUseCase
import com.runvpn.app.data.subscription.domain.SubscriptionRepository
import org.koin.dsl.module


val dataSubscriptionsModule = module {
    single<SubscriptionsApi> {
        get<NetworkApiFactory>().createAuthorizedKtorfit(
            getTokenDelegate = {
                return@createAuthorizedKtorfit get<AppSettingsRepository>().appToken ?: ""
            }
        ).create()
    }
    single<SubscriptionRepository> { DefaultSubscriptionRepository(get()) }

    factory { GetAllTariffsUseCase(get()) }
    factory { GetMinimumRateUseCase(get()) }
    factory { CalculateCostByDeviceAndPeriodUseCase(get()) }
    factory { CalculateRateCostUseCase() }
    factory { SendOrderAndGetWalletsUseCase(get(), get()) }
    factory { ActivateSubscriptionUseCase(get()) }
    factory { MoveSubscriptionUseCase(get()) }
    factory { GetAvailableUserSubscriptionsUseCase(get()) }
    factory { GetActiveSubscriptionsUseCase(get()) }
    factory { GetDevicesWithTariffUseCase(get(), get()) }
    factory { BuySubscriptionUseCase(get()) }
    factory { GetWalletsUseCase(get()) }
    factory { GetAvailableTariffsUseCase(get()) }
    factory { GetSubscriptionsCountUseCase(get()) }
}
