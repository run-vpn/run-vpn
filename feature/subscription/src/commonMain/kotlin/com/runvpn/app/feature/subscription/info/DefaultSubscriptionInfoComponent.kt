package com.runvpn.app.feature.subscription.info

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.subscription.domain.GetAvailableUserSubscriptionsUseCase
import com.runvpn.app.data.subscription.domain.GetDevicesWithTariffUseCase
import com.runvpn.app.data.subscription.domain.GetSubscriptionsCountUseCase
import com.runvpn.app.feature.subscription.SubscriptionInfoFeature
import com.runvpn.app.tea.decompose.BaseComponent
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage

internal class DefaultSubscriptionInfoComponent(
    componentContext: ComponentContext,
    deviceRepository: DeviceRepository,
    appSettingsRepository: AppSettingsRepository,
    getUserSubscriptionsUseCase: GetAvailableUserSubscriptionsUseCase,
    getDevicesWithTariffUseCase: GetDevicesWithTariffUseCase,
    getSubscriptionsCountUseCase: GetSubscriptionsCountUseCase,
    private val messageService: MessageService,
    private val onOutput: (SubscriptionInfoComponent.Output) -> Unit,
) : BaseComponent<SubscriptionInfoFeature.State, SubscriptionInfoFeature.Message, SubscriptionInfoFeature.Dependencies>(
    initialState = SubscriptionInfoFeature.Logic.initialUpdate,
    restore = SubscriptionInfoFeature.Logic::restore,
    update = SubscriptionInfoFeature.Logic::update,
    dependencies = SubscriptionInfoFeature.Dependencies(
        deviceRepository = deviceRepository,
        settingsRepository = appSettingsRepository,
        getUserSubscriptionsUseCase = getUserSubscriptionsUseCase,
        getDevicesWithTariffUseCase = getDevicesWithTariffUseCase,
        getSubscriptionsCountUseCase = getSubscriptionsCountUseCase
    )
), SubscriptionInfoComponent, ComponentContext by componentContext {

    init {
        lifecycle.doOnResume {
            dispatch(SubscriptionInfoFeature.Message.OnResume)
        }
    }

    override fun onBuySubscriptionClick() {
        onOutput(SubscriptionInfoComponent.Output.BuySubscriptionRequested)
    }

    override fun onSwitchTariffClick() {
        onOutput(SubscriptionInfoComponent.Output.SwitchTariffRequested)
    }

    override fun onActivateSubscriptionClicked() {
        onOutput(SubscriptionInfoComponent.Output.SubscriptionActivateRequested)
    }

    override fun onGiveSubscriptionClicked() {
        messageService.showMessage(AppMessage.NotImplemented())
    }

    override fun onDeviceClick(device: Device) {
        onOutput(SubscriptionInfoComponent.Output.AboutDeviceRequested(device = device))
    }

    override fun onBackClick() {
        onOutput(SubscriptionInfoComponent.Output.OnBack)
    }
}
