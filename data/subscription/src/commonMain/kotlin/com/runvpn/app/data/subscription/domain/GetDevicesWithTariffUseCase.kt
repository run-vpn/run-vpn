package com.runvpn.app.data.subscription.domain

import com.runvpn.app.data.device.domain.models.device.DeviceWithTariff
import com.runvpn.app.data.device.domain.usecases.user.GetUserDevicesUseCase
import com.runvpn.app.data.settings.domain.Tariff

/** Этот UseCase обязывает модуль [data.subscription] быть зависимым от модуля [data.devices]*/
class GetDevicesWithTariffUseCase(
    private val getActiveSubscriptionsUseCase: GetActiveSubscriptionsUseCase,
    private val getUserDevicesUseCase: GetUserDevicesUseCase
) {

    /**Возвращает список устройств и их тарифов [DeviceWithTariff]
     * тариф [Tariff] расчитывается из текущей подписки девайса */
    suspend operator fun invoke() = runCatching {
        val devicesResult = getUserDevicesUseCase()
        val subscriptionsResult = getActiveSubscriptionsUseCase()

        if (devicesResult.isSuccess && subscriptionsResult.isSuccess) {

            val devices = devicesResult.getOrThrow()

            val subscriptions = subscriptionsResult.getOrThrow()

            val devicesWithTariff = devices.map { device ->

                val tariff = when {
                    device.latestSubscriptionUuid == null -> Tariff.FREE
                    subscriptions.any { subs -> subs.id == device.latestSubscriptionUuid } -> Tariff.PAID
                    else -> Tariff.EXPIRED
                }

                DeviceWithTariff(
                    device = device,
                    tariff = tariff
                )
            }
            return@runCatching devicesWithTariff
        } else {
            return@runCatching listOf()
        }
    }

}
