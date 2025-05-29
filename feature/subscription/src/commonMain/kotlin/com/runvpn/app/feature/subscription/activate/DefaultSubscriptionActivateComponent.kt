package com.runvpn.app.feature.subscription.activate

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.device.domain.usecases.user.GetUserDevicesUseCase
import com.runvpn.app.data.subscription.domain.GetAvailableUserSubscriptionsUseCase
import com.runvpn.app.data.subscription.domain.entities.Subscription
import com.runvpn.app.feature.common.createShareQrCodeComponent
import com.runvpn.app.feature.common.dialogs.DialogComponent
import com.runvpn.app.feature.subscription.createShareActivationCodeComponent
import com.runvpn.app.feature.subscription.createShareApkComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.createCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class DefaultSubscriptionActivateComponent(
    componentContext: ComponentContext,
    private val componentFactory: DecomposeComponentFactory,
    private val getUserDevicesUseCase: GetUserDevicesUseCase,
    private val getAvailableUserSubscriptionsUseCase: GetAvailableUserSubscriptionsUseCase,
    private val output: (SubscriptionActivateComponent.Output) -> Unit
) : SubscriptionActivateComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope()

    private val _state =
        MutableValue(
            SubscriptionActivateComponent.State(
                selectedDevice = null,
                selectedSubscription = null,
                allDevices = listOf(),
                subscriptions = listOf()
            )
        )
    override val state: Value<SubscriptionActivateComponent.State> = _state

    private val dialogNavigation = SlotNavigation<DialogConfig>()
    override val dialog: Value<ChildSlot<*, DialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = DialogConfig.serializer(),
        handleBackButton = false
    ) { configuration, componentContext ->


        when (configuration) {
            is DialogConfig.ShareApkFileDialog -> {
                return@childSlot componentFactory.createShareApkComponent(
                    componentContext,
                    configuration.link,
                    dialogNavigation::dismiss
                )
            }

            is DialogConfig.ShareQrCodeDialog -> {
                return@childSlot componentFactory.createShareQrCodeComponent(
                    componentContext,
                    configuration.link,
                    dialogNavigation::dismiss
                )
            }

            is DialogConfig.ShareActivationCodeDialog -> {
                return@childSlot componentFactory.createShareActivationCodeComponent(
                    componentContext,
                    configuration.code,
                    dialogNavigation::dismiss
                )
            }
        }
    }

    init {
        coroutineScope.launch {
            val devices = getUserDevicesUseCase().getOrThrow()
            val subscriptions = getAvailableUserSubscriptionsUseCase().getOrThrow()

            _state.value = state.value.copy(
                allDevices = devices,
                subscriptions = subscriptions
            )

            if (devices.size == 1) {
                _state.value = state.value.copy(
                    selectedDevice = devices.firstOrNull(),
                )
            }

            if (subscriptions.size == 1) {
                _state.value = state.value.copy(
                    selectedSubscription = subscriptions.firstOrNull(),
                )
            }
        }
    }

    override fun onChooseDevice(deviceDto: Device) {
        _state.value = state.value.copy(selectedDevice = deviceDto)
    }

    override fun onChooseSubscription(subscription: Subscription) {
        _state.value = state.value.copy(selectedSubscription = subscription)
    }

    override fun onActivateClick() {
        val subscriptionId = state.value.selectedSubscription?.id ?: return
        val device = state.value.selectedDevice ?: return

        output(
            SubscriptionActivateComponent.Output.OnConfirmScreenRequested(
                subscriptionId = subscriptionId,
                device = device
            )
        )
    }

    override fun onShareApkFileClick() {
        dialogNavigation.activate(DialogConfig.ShareApkFileDialog("vpn.run/latest.apk?ref=vasya"))
    }

    override fun onShareQrClick() {
        dialogNavigation.activate(DialogConfig.ShareQrCodeDialog("vpn.run/latest.apk?ref=vasya"))
    }

    override fun onActivationCodeClick() {
        dialogNavigation.activate(DialogConfig.ShareActivationCodeDialog("8435df448df3423"))
    }

    override fun onBackClicked() {
        output(SubscriptionActivateComponent.Output.OnBack)
    }

    @Serializable
    sealed interface DialogConfig {

        /** Activate this Config in dialogNavigation, to show Share BottomSheet*/
        @Serializable
        data class ShareApkFileDialog(val link: String) : DialogConfig

        @Serializable
        data class ShareQrCodeDialog(val link: String) : DialogConfig

        @Serializable
        data class ShareActivationCodeDialog(val code: String) : DialogConfig
    }
}
