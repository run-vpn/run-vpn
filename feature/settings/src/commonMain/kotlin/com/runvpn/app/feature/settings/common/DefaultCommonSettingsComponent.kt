package com.runvpn.app.feature.settings.common

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.feature.settings.createChooseSuggestedMode
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import com.runvpn.app.tea.dialog.DialogManager
import com.runvpn.app.tea.dialog.DialogMessage
import com.runvpn.app.tea.dialog.RootDialogConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class DefaultCommonSettingsComponent(
    componentContext: ComponentContext,
    exceptionHandler: CoroutineExceptionHandler,
    private val componentFactory: DecomposeComponentFactory,
    private val appSettingsRepository: AppSettingsRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val vpnConnectionManager: VpnConnectionManager,
    private val dialogManager: DialogManager
) : CommonSettingsComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val dialogNavigation: SlotNavigation<SlotConfig> = SlotNavigation()

    override val childSlot: Value<ChildSlot<*, SimpleDialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = SlotConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createDialogChild
    )

    private val _state = MutableValue(
        CommonSettingsComponent.State(
            runAfterDeviceStart = userSettingsRepository.runAfterDeviceStart,
            autoDisconnect = appSettingsRepository.timeToShutdown != 0L,
            connectOnNetworkEnabled = userSettingsRepository.connectOnNetworkEnabled,
            reconnectToNextServer = userSettingsRepository.reconnectToNextServer,
            autoConnect = userSettingsRepository.autoConnect,
            suggestedServersMode = SuggestedServersMode.DEFAULT,
            tactileFeedback = userSettingsRepository.tactileFeedbackOnConnect
        )
    )

    override val state: Value<CommonSettingsComponent.State>
        get() = _state

    init {
        coroutineScope.launch {
            userSettingsRepository.suggestedServersMode.collectLatest {
                _state.value = state.value.copy(suggestedServersMode = it)
            }
        }
    }

    private fun createDialogChild(
        childConfig: SlotConfig,
        childComponentContext: ComponentContext
    ): SimpleDialogComponent {
        return when (childConfig) {
            SlotConfig.ChooseSuggestedServersMode -> componentFactory.createChooseSuggestedMode(
                childComponentContext,
                onSuggestedModeChoose = ::onSuggestedServersModeChanged,
                onDismissed = { dialogNavigation.dismiss() }
            )
        }
    }



    override fun onStartupToggled(isEnabled: Boolean) {
        _state.value = state.value.copy(runAfterDeviceStart = isEnabled)
        userSettingsRepository.runAfterDeviceStart = isEnabled
    }

    override fun onSuggestedServersModeChanged(mode: SuggestedServersMode) {
        _state.value = state.value.copy(suggestedServersMode = mode)
        userSettingsRepository.setSuggestedServersMode(mode)
    }

    override fun onReconnectToNextServerToggled(isEnabled: Boolean) {
        _state.value = state.value.copy(reconnectToNextServer = isEnabled)
        userSettingsRepository.reconnectToNextServer = isEnabled
    }

    override fun onConnectOnNetworkEnabledToggled(isEnabled: Boolean) {
        _state.value = state.value.copy(connectOnNetworkEnabled = isEnabled)
        userSettingsRepository.connectOnNetworkEnabled = isEnabled
    }

    override fun onAutoDisconnectToggled(isEnabled: Boolean, timeToShutdown: Long) {
        if (vpnConnectionManager.connectionStatus.value.isDisconnected().not()) {
            showReconnectDialog()
        }
        _state.value = state.value.copy(autoDisconnect = isEnabled)
        if (isEnabled) { //there will be a long instead boolean to directly set as time
            appSettingsRepository.timeToShutdown = timeToShutdown //10 minute (demo)
        } else {
            appSettingsRepository.timeToShutdown = 0L
        }
    }

    override fun onTactileFeedbackToggled(isEnabled: Boolean) {
        _state.value = state.value.copy(tactileFeedback = isEnabled)
        userSettingsRepository.tactileFeedbackOnConnect = isEnabled
    }

    override fun onAutoConnectToggled(isEnabled: Boolean) {
        _state.value = state.value.copy(autoConnect = isEnabled)
        userSettingsRepository.autoConnect = isEnabled
    }

    override fun onChangeSuggestedServersModeClicked() {
        dialogNavigation.activate(SlotConfig.ChooseSuggestedServersMode)
    }

    private fun showReconnectDialog() {
        dialogManager.showDialog(
            RootDialogConfig.AlertDialog(
                DialogMessage.ReconnectVPN,
                onConfirm = {
                    vpnConnectionManager.reconnectToLatestServer()
                    dialogManager.dismiss()
                },
                onDismiss = {
                    dialogManager.dismiss()
                })
        )
    }


    @Serializable
    sealed interface SlotConfig {

        @Serializable
        data object ChooseSuggestedServersMode : SlotConfig

    }

}
