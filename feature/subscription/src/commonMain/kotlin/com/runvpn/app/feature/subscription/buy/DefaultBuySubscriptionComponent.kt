package com.runvpn.app.feature.subscription.buy

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnStart
import com.runvpn.app.data.device.domain.usecases.user.GetUserDevicesUseCase
import com.runvpn.app.data.device.domain.usecases.user.GetUserShortDataUseCase
import com.runvpn.app.data.subscription.domain.BuySubscriptionUseCase
import com.runvpn.app.data.subscription.domain.CalculateRateCostUseCase
import com.runvpn.app.data.subscription.domain.GetMinimumRateUseCase
import com.runvpn.app.data.subscription.domain.entities.ChosenSubscription
import com.runvpn.app.feature.subscription.createChooseSubscriptionTariffComponent
import com.runvpn.app.feature.subscription.tariff.ChooseRateComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.utils.safeLaunch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class DefaultBuySubscriptionComponent(
    componentContext: ComponentContext,
    private val componentFactory: DecomposeComponentFactory,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val getUserShortDataUseCase: GetUserShortDataUseCase,
    private val calculateRateCostUseCase: CalculateRateCostUseCase,
    private val getMinimumRateUseCase: GetMinimumRateUseCase,
    private val buySubscriptionUseCase: BuySubscriptionUseCase,
    private val getUserDevicesUseCase: GetUserDevicesUseCase,
    private val messageService: MessageService,
    private val output: (BuySubscriptionComponent.Output) -> Unit
) : BuySubscriptionComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val _state: MutableValue<BuySubscriptionComponent.State> = MutableValue(
        BuySubscriptionComponent.State.Loading
    )
    override val state: Value<BuySubscriptionComponent.State> = _state

    private val dialogNavigation: SlotNavigation<DialogConfig> = SlotNavigation()
    override val dialogSlot: Value<ChildSlot<*, SimpleDialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = DialogConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createDialogChild
    )

    private fun createDialogChild(
        config: DialogConfig,
        componentContext: ComponentContext
    ): SimpleDialogComponent = when (config) {
        DialogConfig.ChooseRate -> {
            val loadedState = (_state.value as? BuySubscriptionComponent.State.Loaded)
                ?: error("State is not loaded")

            componentFactory.createChooseSubscriptionTariffComponent(
                componentContext = componentContext,
                initialChosenSubscription = ChosenSubscription(
                    rate = loadedState.chosenTariff,
                    deviceCount = loadedState.deviceCount,
                    periodInDays = loadedState.periodInDays
                ),
                onOutput = ::handleChooseTariffOutput
            )
        }
    }

    init {
        lifecycle.doOnStart {
            coroutineScope.launch {
                val devices = getUserDevicesUseCase().getOrThrow()

                val balance = getUserShortDataUseCase().getOrThrow().balanceInDollar
                val minimumRate = getMinimumRateUseCase()

                val cost = calculateRateCostUseCase(
                    minimumRate,
                    deviceCount = minimumRate.deviceCountMax,
                    periodInDays = minimumRate.periodInDays
                )

                _state.value = BuySubscriptionComponent.State.Loaded(
                    balance = balance,
                    chosenTariff = minimumRate,
                    deviceCount = minimumRate.deviceCountMax,
                    selectedDeviceUuids = listOf(),
                    devices = devices,
                    periodInDays = minimumRate.periodInDays,
                    calculatedCost = cost,
                    isEnoughFunds = balance >= cost
                )
            }
        }
    }

    override fun onEditTariffClicked() {
        dialogNavigation.activate(DialogConfig.ChooseRate)
    }

    override fun onBuyClicked() {
        val loadedState = (state.value as? BuySubscriptionComponent.State.Loaded) ?: return

        if (loadedState.isEnoughFunds.not()) {
            refillBalanceRequest()
            return
        }

        coroutineScope.safeLaunch(finallyBlock = {
            (_state.value as? BuySubscriptionComponent.State.Loaded)?.let {
                _state.value = it.copy(isBuyingLoading = false)
            }
        }) {
            (_state.value as? BuySubscriptionComponent.State.Loaded)?.let {
                _state.value = it.copy(isBuyingLoading = true)
            }

            buySubscriptionUseCase(
                tariffUUID = loadedState.chosenTariff.id,
                deviceCount = loadedState.deviceCount,
                deviceUuids = loadedState.selectedDeviceUuids
            ).onSuccess {
                messageService.showMessage(AppMessage.SuccessSubscriptionBuying())
                output(BuySubscriptionComponent.Output.OnBack)
            }
        }
    }

    override fun onActivateDevicesSelected(deviceUuid: String) {
        (_state.value as? BuySubscriptionComponent.State.Loaded)?.let {
            val mutableSelectedDevices = it.selectedDeviceUuids.toMutableList()
            if (mutableSelectedDevices.contains(deviceUuid)) {
                mutableSelectedDevices.remove(deviceUuid)
            } else {
                mutableSelectedDevices.add(deviceUuid)
            }
            _state.value = it.copy(selectedDeviceUuids = mutableSelectedDevices)
        }
    }

    override fun onBackClicked() {
        output(BuySubscriptionComponent.Output.OnBack)
    }

    private fun refillBalanceRequest() {
        (_state.value as? BuySubscriptionComponent.State.Loaded)?.calculatedCost?.let {
            output(BuySubscriptionComponent.Output.OnRefillBalanceRequested(it))
        }
    }

    private fun updateTariffInfo(subscription: ChosenSubscription) {
        coroutineScope.launch {
            val balance = (_state.value as? BuySubscriptionComponent.State.Loaded)?.balance ?: 0.0
            val devices = (_state.value as? BuySubscriptionComponent.State.Loaded)?.devices ?: listOf()

            val cost = calculateRateCostUseCase(
                subscription.rate,
                deviceCount = subscription.deviceCount,
                periodInDays = subscription.periodInDays
            )

            _state.value = BuySubscriptionComponent.State.Loaded(
                balance = balance,
                chosenTariff = subscription.rate,
                deviceCount = subscription.deviceCount,
                selectedDeviceUuids = listOf(),
                devices = devices,
                periodInDays = subscription.periodInDays,
                calculatedCost = cost,
                isEnoughFunds = balance >= cost
            )
        }
    }

    private fun handleChooseTariffOutput(output: ChooseRateComponent.Output) {
        when (output) {
            ChooseRateComponent.Output.Dismiss -> dialogNavigation.dismiss()
            ChooseRateComponent.Output.OrderCreated -> {}
            is ChooseRateComponent.Output.RateChosen -> {
                dialogNavigation.dismiss()
                updateTariffInfo(output.chosenSubscription)
            }
        }
    }

    @Serializable
    sealed interface DialogConfig {

        @Serializable
        data object ChooseRate : DialogConfig
    }
}
