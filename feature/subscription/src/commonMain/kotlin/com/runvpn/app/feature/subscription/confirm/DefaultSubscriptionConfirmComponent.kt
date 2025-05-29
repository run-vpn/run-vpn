package com.runvpn.app.feature.subscription.confirm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.subscription.domain.ActivateSubscriptionUseCase
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.utils.safeLaunch
import kotlinx.coroutines.CoroutineExceptionHandler

class DefaultSubscriptionConfirmComponent(
    componentContext: ComponentContext,
    private val subscriptionActivateUseCase: ActivateSubscriptionUseCase,
    private val messageService: MessageService,
    exceptionHandler: CoroutineExceptionHandler,
    subscriptionId: String,
    device: Device,
    private val output: (SubscriptionConfirmComponent.Output) -> Unit
) : SubscriptionConfirmComponent, ComponentContext by componentContext {

    private val _state = MutableValue(
        SubscriptionConfirmComponent.State(
            subscriptionId = subscriptionId,
            device = device,
            isLoading = false
        )
    )
    override val state: Value<SubscriptionConfirmComponent.State>
        get() = _state

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    override fun onConfirmClicked() {
        _state.value = state.value.copy(isLoading = true)
        coroutineScope.safeLaunch(
            finallyBlock = {
                _state.value = state.value.copy(isLoading = false)
            }
        ) {
            subscriptionActivateUseCase(
                subscriptionId = state.value.subscriptionId,
                deviceId = state.value.device.uuid
            ).onSuccess {
                messageService.showMessage(
                    AppMessage.SubscriptionActivated(
                        deviceName = state.value.device.hardware.brand,
                        activeUntil = it.finishedAt?.toEpochMilliseconds() ?: 0
                    )
                )
                output(SubscriptionConfirmComponent.Output.OnActivationSuccess)
            }
        }
    }
}
