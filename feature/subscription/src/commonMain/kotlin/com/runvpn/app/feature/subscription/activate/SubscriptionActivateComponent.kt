package com.runvpn.app.feature.subscription.activate

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.subscription.domain.entities.Subscription
import com.runvpn.app.feature.common.dialogs.DialogComponent

interface SubscriptionActivateComponent {

    val state: Value<State>

    val dialog: Value<ChildSlot<*, DialogComponent>>

    data class State(
        val selectedDevice: Device?,
        val selectedSubscription: Subscription?,
        val allDevices: List<Device>,
        val subscriptions: List<Subscription>
    )

    fun onChooseDevice(deviceDto: Device)
    fun onChooseSubscription(subscription: Subscription)

    fun onActivateClick()
    fun onShareApkFileClick()
    fun onShareQrClick()
    fun onActivationCodeClick()

    fun onBackClicked()


    sealed interface Output {
        data object OnBack : Output

        data class OnConfirmScreenRequested(val subscriptionId: String, val device: Device) : Output
    }

}
