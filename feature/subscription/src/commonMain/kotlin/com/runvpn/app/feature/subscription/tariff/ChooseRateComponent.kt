package com.runvpn.app.feature.subscription.tariff

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.subscription.data.network.Rate
import com.runvpn.app.data.subscription.domain.entities.ChosenSubscription
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface ChooseRateComponent : SimpleDialogComponent {

    companion object {
        const val MIN_DEVICE_COUNT = 1
        const val MAX_DEVICE_COUNT = 10

        const val MIN_DAYS_COUNT = 10
        const val MAX_DAYS_COUNT = 90
    }

    data class State(
        val balance: Float,
        val deviceCount: Int,
        val daysCount: Int,
        val cost: Long,
        val minDeviceCount: Int = 1,
        val maxDeviceCount: Int = 1,
        val minPeriodInDays: Int = 1,
        val maxPeriodInDays: Int = 1,
        val isLoading: Boolean,
        val isRateNotFound: Boolean,
        val allAvailableTariffs: List<Rate>,
        val availableDeviceCount: Set<Int>
    )

    val state: Value<State>

    val payWithCryptoDialogChild: Value<ChildSlot<*, SimpleDialogComponent>>

    val minDeviceCount: Int
        get() = MIN_DEVICE_COUNT
    val maxDeviceCount: Int
        get() = MAX_DEVICE_COUNT
    val minDaysCount: Int
        get() = MIN_DAYS_COUNT
    val maxDaysCount: Int
        get() = MAX_DAYS_COUNT

    fun onDeviceChanged(newValue: Int)
    fun onDaysChanged(newValue: Int)

    fun onBuyClicked()

    sealed interface Output {
        data object OrderCreated : Output
        data class RateChosen(val chosenSubscription: ChosenSubscription) : Output
        data object Dismiss : Output
    }
}
