package com.runvpn.app.feature.subscription.confirm

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.TestDataDevices

class FakeSubscriptionConfirmComponent : SubscriptionConfirmComponent {
    override val state: Value<SubscriptionConfirmComponent.State>
        get() = MutableValue(
            SubscriptionConfirmComponent.State(
                subscriptionId = "",
                TestDataDevices.testDevicesList[0],
                isLoading = false
            )
        )

    override fun onConfirmClicked() {
        TODO("Not yet implemented")
    }
}
