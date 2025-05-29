package com.runvpn.app.feature.subscription.activate

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.subscription.domain.entities.Subscription
import com.runvpn.app.feature.common.dialogs.DialogComponent

class FakeSubscriptionActivateComponent : SubscriptionActivateComponent {
    override val state: Value<SubscriptionActivateComponent.State>
        get() = MutableValue(
            SubscriptionActivateComponent.State(
                null,
                null,
                listOf(),
                listOf()
            )
        )
    override val dialog: Value<ChildSlot<*, DialogComponent>>
        get() = MutableValue(ChildSlot<Nothing, Nothing>())

    override fun onChooseDevice(deviceDto: Device) {
        TODO("Not yet implemented")
    }

    override fun onChooseSubscription(subscription: Subscription) {
        TODO("Not yet implemented")
    }

    override fun onActivateClick() {
        TODO("Not yet implemented")
    }

    override fun onShareApkFileClick() {
        TODO("Not yet implemented")
    }

    override fun onShareQrClick() {
        TODO("Not yet implemented")
    }

    override fun onActivationCodeClick() {
        TODO("Not yet implemented")
    }

    override fun onBackClicked() {
        TODO("Not yet implemented")
    }
}
