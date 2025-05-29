package com.runvpn.app.feature.subscription.info

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.feature.subscription.SubscriptionInfoFeature

interface SubscriptionInfoComponent {

    val state: Value<SubscriptionInfoFeature.State>

    fun onBuySubscriptionClick()
    fun onSwitchTariffClick()

    fun onActivateSubscriptionClicked()
    fun onGiveSubscriptionClicked()
    fun onDeviceClick(device: Device)

    fun onBackClick()

    sealed interface Output {
        data object BuySubscriptionRequested : Output
        data object SubscriptionActivateRequested : Output
        data object SwitchTariffRequested : Output
        data class AboutDeviceRequested(val device: Device) : Output
        data object OnBack : Output
    }
}
