package com.runvpn.app.feature.subscription.tariff

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.runvpn.app.data.subscription.data.network.Rate
import com.runvpn.app.data.subscription.domain.CalculateCostByDeviceAndPeriodUseCase
import com.runvpn.app.data.subscription.domain.CalculateCostResult
import com.runvpn.app.data.subscription.domain.GetAvailableTariffsUseCase
import com.runvpn.app.data.subscription.domain.entities.ChosenSubscription
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost
import com.runvpn.app.feature.subscription.createBuyWithCryptoComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

internal class DefaultChooseRateComponent(
    componentContext: ComponentContext,
    private val initialChosenSubscription: ChosenSubscription,
    private val componentFactory: DecomposeComponentFactory,
    private val getAvailableTariffsUseCase: GetAvailableTariffsUseCase,
    private val calculateCostByDeviceAndPeriodUseCase: CalculateCostByDeviceAndPeriodUseCase,
    private val coroutineExceptionHandler: CoroutineExceptionHandler,
    private val onOutput: (ChooseRateComponent.Output) -> Unit
) : ChooseRateComponent, ComponentContext by componentContext {

    private val componentScope = createCoroutineScope(coroutineExceptionHandler)

    private val _state: MutableValue<ChooseRateComponent.State> = MutableValue(
        ChooseRateComponent.State(
            balance = 0.0f,
            deviceCount = initialChosenSubscription.deviceCount,
            daysCount = initialChosenSubscription.periodInDays,
            cost = 0,
            isLoading = false,
            isRateNotFound = false,
            allAvailableTariffs = listOf(),
            availableDeviceCount = setOf()
        )
    )
    override val state: Value<ChooseRateComponent.State> = _state

    private val dialogNavigation: SlotNavigation<DialogConfig> = SlotNavigation()
    override val payWithCryptoDialogChild: Value<ChildSlot<*, SimpleDialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = DialogConfig.serializer(),
        handleBackButton = true
    ) { childConfig, childComponentContext ->
        return@childSlot componentFactory.createBuyWithCryptoComponent(
            childComponentContext,
            childConfig.wallets,
            onDismissed = {
                dialogNavigation.dismiss()
                onOutput(ChooseRateComponent.Output.OrderCreated)
            }
        )
    }

    private var chosenRate: Rate? = null

    init {
        lifecycle.doOnCreate {
            componentScope.launch {
                getAvailableTariffsUseCase()
                    .onSuccess { tariffs ->
                        val deviceCountList: MutableSet<Int> = mutableSetOf()
                        for (t in tariffs) {
                            deviceCountList.addAll(
                                (t.deviceCountMin..t.deviceCountMax).toList()
                            )
                        }

                        _state.value = _state.value.copy(
                            allAvailableTariffs = tariffs,
                            minDeviceCount = tariffs.minOf { it.deviceCountMin },
                            maxDeviceCount = tariffs.maxOf { it.deviceCountMax },
                            minPeriodInDays = tariffs.minOf { it.periodInDays },
                            maxPeriodInDays = tariffs.maxOf { it.periodInDays },
                            availableDeviceCount = deviceCountList.sorted().toSet()
                        )

                        calculateCost(
                            deviceCount = _state.value.deviceCount,
                            daysCount = _state.value.daysCount
                        )
                    }
            }
        }
    }

    override fun onDeviceChanged(newValue: Int) {
        _state.value = _state.value.copy(deviceCount = newValue)

        calculateCost(newValue, _state.value.daysCount)
    }

    override fun onDaysChanged(newValue: Int) {
        _state.value = _state.value.copy(daysCount = newValue)

        calculateCost(_state.value.deviceCount, newValue)
    }

    override fun onBuyClicked() {
        chosenRate?.let {
            onOutput(
                ChooseRateComponent.Output.RateChosen(
                    ChosenSubscription(
                        rate = it,
                        deviceCount = state.value.deviceCount,
                        periodInDays = state.value.daysCount
                    )
                )
            )
        }
    }

    override fun onDismissClicked() {
        onOutput(ChooseRateComponent.Output.Dismiss)
    }

    private fun calculateCost(deviceCount: Int, daysCount: Int) {
        componentScope.launch {
            when (val result = calculateCostByDeviceAndPeriodUseCase(deviceCount, daysCount)) {
                is CalculateCostResult.Success -> {
                    this@DefaultChooseRateComponent.chosenRate = result.rate
                    _state.value = _state.value.copy(
                        cost = result.cost,
                        isRateNotFound = false
                    )
                }

                CalculateCostResult.RateNotFound -> {
                    _state.value = _state.value.copy(isRateNotFound = true)
                }
            }
        }
    }

    @Serializable
    private data class DialogConfig(val wallets: List<WalletWithRateCost>)
}
