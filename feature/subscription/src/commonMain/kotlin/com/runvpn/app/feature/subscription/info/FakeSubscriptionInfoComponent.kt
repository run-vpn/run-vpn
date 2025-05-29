package com.runvpn.app.feature.subscription.info

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.TestDataDevices
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.subscription.domain.entities.Subscription
import com.runvpn.app.data.subscription.domain.entities.SubscriptionsCount
import com.runvpn.app.feature.subscription.SubscriptionInfoFeature
import kotlinx.datetime.Clock

class FakeSubscriptionInfoComponent : SubscriptionInfoComponent {

    override val state: Value<SubscriptionInfoFeature.State> = MutableValue(
        SubscriptionInfoFeature.State(
            listOf(),
            isLoading = false,
            isError = false,
            userShortData = TestDataDevices.testUser,
            subscriptions = listOf(
                Subscription(
                    id = "8097a852-9236-429b-b98e-ac907b1b8601",
                    deviceUuid = "8097a852-9236-429b-b98e-ac907b1b8601",
                    periodInDays = 3,
                    expirationAt = Clock.System.now(),
                    finishedAt = null
                ),
                Subscription(
                    id = "17d9fb4e-ccb7-4047-b9a8-10c7fd7cd4e8",
                    deviceUuid = "17d9fb4e-ccb7-4047-b9a8-10c7fd7cd4e8",
                    periodInDays = 2,
                    expirationAt = Clock.System.now(),
                    finishedAt = null
                )
            ),
            currentDeviceUuid = null,
            subscriptionsCount = SubscriptionsCount.DEFAULT
        )
    )

    override fun onBuySubscriptionClick() {
        TODO("Not yet implemented")
    }

    override fun onSwitchTariffClick() {
        TODO("Not yet implemented")
    }

    override fun onActivateSubscriptionClicked() {
        TODO("Not yet implemented")
    }

    override fun onGiveSubscriptionClicked() {
        TODO("Not yet implemented")
    }

    override fun onDeviceClick(device: Device) {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }
}
