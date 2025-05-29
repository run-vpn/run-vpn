package com.runvpn.app.feature.subscription.buy

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.subscription.data.network.Rate
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface BuySubscriptionComponent {

    sealed interface State {
        data object Loading : State
        data class Loaded(
            val balance: Double,
            val chosenTariff: Rate,
            val deviceCount: Int,
            val devices: List<Device>,
            val selectedDeviceUuids: List<String>,
            val periodInDays: Int,
            val calculatedCost: Double,
            val isEnoughFunds: Boolean,
            val isBuyingLoading: Boolean = false
        ) : State
    }

    val state: Value<State>
    val dialogSlot: Value<ChildSlot<*, SimpleDialogComponent>>

    fun onEditTariffClicked()
    fun onBuyClicked()
    fun onActivateDevicesSelected(deviceUuid: String)

    fun onBackClicked()

    sealed interface Output {
        data object OnBack : Output

        data class OnRefillBalanceRequested(val cost: Double) : Output
    }
}
