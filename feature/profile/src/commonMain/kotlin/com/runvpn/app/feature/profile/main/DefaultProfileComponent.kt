package com.runvpn.app.feature.profile.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnResume
import com.runvpn.app.data.device.domain.usecases.user.GetUserDevicesUseCase
import com.runvpn.app.data.device.domain.usecases.user.GetUserShortDataUseCase
import com.runvpn.app.data.settings.domain.Tariff
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.subscription.domain.GetActiveSubscriptionsUseCase
import com.runvpn.app.tea.LogoutHandler
import com.runvpn.app.tea.SyncDataRequest
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.navigation.RootChildConfig
import com.runvpn.app.tea.navigation.RootRouter
import com.runvpn.app.tea.utils.safeLaunch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class DefaultProfileComponent(
    componentContext: ComponentContext,
    private val appSettingsRepository: AppSettingsRepository,
    private val getUserShortDataUseCase: GetUserShortDataUseCase,
    private val getUserDevicesUseCase: GetUserDevicesUseCase,
    private val getSubscriptionsUseCase: GetActiveSubscriptionsUseCase,
    exceptionHandler: CoroutineExceptionHandler,
    private val rootRouter: RootRouter,
    private val messageService: MessageService,
    private val logoutHandler: LogoutHandler,
    private val syncDataRequest: SyncDataRequest,
    private val onOutput: (ProfileComponent.Output) -> Unit
) : ProfileComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val _state = MutableValue(
        ProfileComponent.State(
            isLoading = false,
            userShortData = null,
            devices = null,
            tariff = null
        )
    )
    override val state: Value<ProfileComponent.State> = _state

    init {
        lifecycle.doOnResume {
            refreshProfileData()
        }
    }

    override fun onAuthClick() {
        onOutput.invoke(ProfileComponent.Output.OnAuthScreenRequest)
    }

    override fun onSubscriptionClick() {
        rootRouter.open(RootChildConfig.SubscriptionRoot)
    }

    override fun onReferralClicked() {
        messageService.showMessage(AppMessage.NotImplemented())
    }

    override fun onLogoutClicked() {
        _state.value = state.value.copy(isLoading = true)
        coroutineScope.safeLaunch(finallyBlock = {
            _state.value = state.value.copy(isLoading = false)
        }) {
            logoutHandler.logout()
            refreshProfileData()
        }
    }

    override fun onTrafficModuleClick() {
        onOutput.invoke(ProfileComponent.Output.OnTrafficModuleScreenRequest)
    }

    override fun onBuySubscriptionClick() {
        onOutput(ProfileComponent.Output.OnBuySubscriptionRequested)
    }

    override fun onActivatePromoClick() {
        onOutput(ProfileComponent.Output.OnActivatePromoRequested)
    }

    override fun onBalanceClicked() {
        rootRouter.open(RootChildConfig.BalanceRefillConfig())
    }

    private fun refreshProfileData() {
        coroutineScope.launch {
            _state.value = state.value.copy(
                isLoading = state.value.userShortData == null ||
                        state.value.devices == null ||
                        state.value.tariff == null
            )

            val userData = getUserShortDataUseCase().getOrThrow()

            val devices = getUserDevicesUseCase().getOrThrow()

            val subscriptions = getSubscriptionsUseCase().getOrThrow()
            val subscriptionId =
                devices.find { it.uuid == appSettingsRepository.deviceUuid }?.latestSubscriptionUuid

            val tariff = when {
                subscriptionId == null -> Tariff.FREE
                subscriptions.any { it.id == subscriptionId } -> Tariff.PAID
                else -> Tariff.EXPIRED
            }

            _state.value = state.value.copy(
                tariff = tariff,
                userShortData = userData,
                devices = devices,
                isLoading = false
            )
        }
    }
}

