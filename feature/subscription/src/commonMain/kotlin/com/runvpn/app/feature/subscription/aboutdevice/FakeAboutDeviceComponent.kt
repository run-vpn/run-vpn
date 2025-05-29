package com.runvpn.app.feature.subscription.aboutdevice

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.TestDataDevices
import com.runvpn.app.data.subscription.domain.entities.Subscription
import kotlinx.datetime.Clock

class FakeAboutDeviceComponent(
    private val isSubscriptionActive: Boolean = false
) : AboutDeviceComponent {
    override val state: Value<AboutDeviceComponent.State>
        get() = MutableValue(
            AboutDeviceComponent.State(
                isLoading = false,
                deviceDto = TestDataDevices.testDevicesList[0],
                deviceName = TestDataDevices.testDevicesList[0].fullName,
                isDeviceNameValid = !TestDataDevices.testDevicesList[0].fullName.isNullOrEmpty(),
                selectedSubscription = Subscription(
                    id = "8097a852-9236-429b-b98e-ac907b1b8601",
                    deviceUuid = "8097a852-9236-429b-b98e-ac907b1b8601",
                    periodInDays = 5,
                    expirationAt = Clock.System.now(),
                    finishedAt = null
                ),
                subscriptions = listOf(),
                isSubscriptionActive = isSubscriptionActive,
                isSubscriptionSelectedError = false,
                isNameChanged = false,
                isNameError = false
            )
        )

    override fun onDeviceNameChange(name: String) {
        TODO("Not yet implemented")
    }

    override fun onSubscriptionChoose(subscription: Subscription) {
        TODO("Not yet implemented")
    }

    override fun onSubscriptionMoveClick() {
        TODO("Not yet implemented")
    }

    override fun onSubscriptionActivateClick() {
        TODO("Not yet implemented")
    }

    override fun onPromoClick() {
        TODO("Not yet implemented")
    }

    override fun onDeviceRemoveClick() {
        TODO("Not yet implemented")
    }

    override fun onSaveDeviceNameClick() {
        TODO("Not yet implemented")
    }

    override fun onCancelDeviceNameEditingClick() {
        TODO("Not yet implemented")
    }

}
