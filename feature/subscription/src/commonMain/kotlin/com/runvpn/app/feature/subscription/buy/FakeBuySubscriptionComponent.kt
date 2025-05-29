package com.runvpn.app.feature.subscription.buy

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.TestDataDevices
import com.runvpn.app.data.subscription.data.network.Rate
import com.runvpn.app.feature.subscription.tariff.ChooseRateComponent

class FakeBuySubscriptionComponent(isLoading: Boolean = false) : BuySubscriptionComponent {

    override val state: Value<BuySubscriptionComponent.State> = MutableValue(
        if (isLoading) {
            BuySubscriptionComponent.State.Loading
        } else {
            BuySubscriptionComponent.State.Loaded(
                balance = 458.10,
                chosenTariff = Rate(
                    isActive = true,
                    deviceCountMin = 1,
                    deviceCountMax = 3,
                    deviceStartCoefficient = 0f,
                    deviceStepCoefficient = 0f,
                    periodInDays = 3,
                    id = "1",
                    priority = 1,
                    promotion = "Tariff's promotion"
                ),
                deviceCount = 1,
                selectedDeviceUuids = listOf(),
                devices = TestDataDevices.testDevicesList,
                periodInDays = 30,
                calculatedCost = 13.54,
                isEnoughFunds = true
            )
        }
    )
    override val dialogSlot: Value<ChildSlot<*, ChooseRateComponent>> = MutableValue(
        ChildSlot<Nothing, Nothing>(null)
    )

    override fun onEditTariffClicked() {
        TODO("Not yet implemented")
    }

    override fun onBuyClicked() {
        TODO("Not yet implemented")
    }

    override fun onActivateDevicesSelected(deviceUuid: String) {
        TODO("Not yet implemented")
    }

    override fun onBackClicked() {
        TODO("Not yet implemented")
    }

}
