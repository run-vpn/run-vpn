package com.runvpn.app.feature.subscription.aboutdevice

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.device.domain.usecases.device.ChangeDeviceNameUseCase
import com.runvpn.app.data.device.domain.usecases.device.DeviceDeleteUseCase
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.subscription.domain.ActivateSubscriptionUseCase
import com.runvpn.app.data.subscription.domain.GetActiveSubscriptionsUseCase
import com.runvpn.app.data.subscription.domain.MoveSubscriptionUseCase
import com.runvpn.app.data.subscription.domain.entities.Subscription
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.utils.safeLaunch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultAboutDeviceComponent(
    componentContext: ComponentContext,
    exceptionHandler: CoroutineExceptionHandler,
    private val deviceDeleteUseCase: DeviceDeleteUseCase,
    private val subscriptionDeleteUseCase: MoveSubscriptionUseCase,
    private val subscriptionActivateUseCase: ActivateSubscriptionUseCase,
    private val getActiveSubscriptionsUseCase: GetActiveSubscriptionsUseCase,
    private val changeDeviceNameUseCase: ChangeDeviceNameUseCase,
    private val appSettingsRepository: AppSettingsRepository,
    private val messageService: MessageService,
    device: Device,
    private val output: (AboutDeviceComponent.Output) -> Unit
) : AboutDeviceComponent, ComponentContext by componentContext {

    companion object {
        private const val DEVICE_NAME_MAX_LENGTH = 30
    }

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val _state = MutableValue(
        AboutDeviceComponent.State(
            isLoading = true,
            deviceDto = device,
            deviceName = device.fullName,
            isDeviceNameValid = true,
            selectedSubscription = null,
            subscriptions = listOf(),
            isSubscriptionActive = false,
            isNameChanged = false,
            isSubscriptionSelectedError = false,
            isRemoveDeviceAvailable = appSettingsRepository.appToken != appSettingsRepository.appAnonymousToken
        )
    )

    override val state: Value<AboutDeviceComponent.State> = _state
    private var tmpDeviceName = device.fullName

    init {
        coroutineScope.launch {
            val userSubscriptionsResponse = getActiveSubscriptionsUseCase()
            if (userSubscriptionsResponse.isSuccess) {
                val subscriptions = userSubscriptionsResponse.getOrThrow()
                val isDeviceSubscriptionActive =
                    subscriptions.any { it.id == device.latestSubscriptionUuid }

                val availableSubscriptions = subscriptions.filter { it.deviceUuid == null }

                _state.value = state.value.copy(
                    subscriptions = availableSubscriptions,
                    isSubscriptionActive = isDeviceSubscriptionActive
                )
            }
            _state.value = state.value.copy(isLoading = false)
        }
    }

    override fun onDeviceNameChange(name: String) {
        if (name.length > DEVICE_NAME_MAX_LENGTH) return
        _state.value = state.value.copy(
            deviceName = name.replace("\\s+".toRegex(), " "),
            isNameChanged = true,
            isNameError = false
        )
    }

    override fun onSubscriptionChoose(subscription: Subscription) {
        _state.value = state.value.copy(
            selectedSubscription = subscription,
            isSubscriptionSelectedError = false
        )
    }

    override fun onSubscriptionMoveClick() {
        messageService.showMessage(AppMessage.NotImplemented())
    }

    override fun onSubscriptionActivateClick() {
        val subscriptionId = state.value.selectedSubscription?.id
        val deviceUuid = state.value.deviceDto.uuid

        if (subscriptionId.isNullOrEmpty()) {
            _state.value = state.value.copy(isSubscriptionSelectedError = true)
            return
        }

        coroutineScope.safeLaunch(finallyBlock = {
            _state.value = _state.value.copy(isSubscriptionActivationLoading = false)
        }) {
            _state.value = _state.value.copy(isSubscriptionActivationLoading = true)

            subscriptionActivateUseCase(subscriptionId = subscriptionId, deviceId = deviceUuid)
                .onSuccess {
                    Logger.d("Finished at: ${it.finishedAt}")
                    Logger.d("Finished at millis: ${it.finishedAt?.toEpochMilliseconds()}")

                    messageService.showMessage(
                        AppMessage.SubscriptionActivated(
                            state.value.deviceName,
                            it.finishedAt?.toEpochMilliseconds() ?: 0
                        )
                    )
                    _state.value = _state.value.copy(
                        deviceDto = _state.value.deviceDto.copy(
                            latestSubscriptionUuid = it.id
                        ),
                        isSubscriptionActive = true
                    )
                }
        }
    }

    override fun onPromoClick() {
        output(AboutDeviceComponent.Output.OnActivatePromoRequested)
    }

    override fun onDeviceRemoveClick() {
        coroutineScope.launch {
            deviceDeleteUseCase(deviceUuid = state.value.deviceDto.uuid)
            output(AboutDeviceComponent.Output.OnBack)
        }
    }

    override fun onSaveDeviceNameClick() {
        if (state.value.deviceName.isNullOrEmpty()) {
            _state.value = state.value.copy(isNameError = true)
            return
        }
        _state.value = _state.value.copy(isNameChangeLoading = true)

        coroutineScope.safeLaunch(finallyBlock = {
            _state.value = _state.value.copy(isNameChangeLoading = false, isNameChanged = false)
        }) {
            withContext(Dispatchers.Default) {
                changeDeviceNameUseCase(
                    state.value.deviceDto.uuid,
                    state.value.deviceName!!
                ).onSuccess {
                    tmpDeviceName = state.value.deviceName
                    messageService.showMessage(AppMessage.DeviceNameChanged())
                }
            }
        }
    }

    override fun onCancelDeviceNameEditingClick() {
        _state.value = state.value.copy(deviceName = tmpDeviceName)
    }

}
