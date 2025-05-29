package com.runvpn.app.feature.subscription.tariff

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.subscription.data.network.Rate
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

class FakeChooseRateComponent : ChooseRateComponent {

    override val state: Value<ChooseRateComponent.State> = MutableValue(
        ChooseRateComponent.State(
            balance = 0f,
            deviceCount = 1,
            daysCount = 30,
            cost = 20L,
            isLoading = false,
            isRateNotFound = false,
            allAvailableTariffs = listOf(
                Rate(
                    id = "uuid1",
                    isActive = true,
                    deviceCountMin = 1,
                    deviceCountMax = 1,
                    deviceStartCoefficient = 500f,
                    deviceStepCoefficient = 500f,
                    periodInDays = 30,
                    priority = 10,
                ),
                Rate(
                    id = "uuid2",
                    isActive = true,
                    deviceCountMin = 3,
                    deviceCountMax = 3,
                    deviceStartCoefficient = 500f,
                    deviceStepCoefficient = 500f,
                    periodInDays = 90,
                    priority = 10,
                ),
                Rate(
                    id = "uuid2",
                    isActive = true,
                    deviceCountMin = 5,
                    deviceCountMax = 5,
                    deviceStartCoefficient = 500f,
                    deviceStepCoefficient = 500f,
                    periodInDays = 360,
                    priority = 10,
                ),
            ),
            availableDeviceCount = setOf(1, 3, 10)
        )
    )
    override val payWithCryptoDialogChild: Value<ChildSlot<*, SimpleDialogComponent>> =
        MutableValue(
            ChildSlot<Nothing, Nothing>(null)
        )

    override fun onDeviceChanged(newValue: Int) {
        TODO("Not yet implemented")
    }

    override fun onDaysChanged(newValue: Int) {
        TODO("Not yet implemented")
    }

    override fun onBuyClicked() {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
