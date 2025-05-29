package com.runvpn.app.feature.settings.common

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface CommonSettingsComponent {

    companion object {
        private const val DEFAULT_SHUTDOWN_TIME = 10 * 60 * 1000L
//        private const val DEFAULT_SHUTDOWN_TIME = 20 * 1000L
    }

    data class State(
        val runAfterDeviceStart: Boolean,
        val suggestedServersMode: SuggestedServersMode,
        val reconnectToNextServer: Boolean,
        val connectOnNetworkEnabled: Boolean,
        val autoConnect: Boolean,
        val autoDisconnect: Boolean,
        val tactileFeedback: Boolean,
    )

    val state: Value<State>

    val childSlot: Value<ChildSlot<*, SimpleDialogComponent>>


    fun onStartupToggled(isEnabled: Boolean)
    fun onSuggestedServersModeChanged(mode: SuggestedServersMode)
    fun onReconnectToNextServerToggled(isEnabled: Boolean)
    fun onConnectOnNetworkEnabledToggled(isEnabled: Boolean)
    fun onAutoDisconnectToggled(isEnabled: Boolean, timeToShutdown: Long = DEFAULT_SHUTDOWN_TIME)
    fun onTactileFeedbackToggled(isEnabled: Boolean)
    fun onAutoConnectToggled(isEnabled: Boolean)
    fun onChangeSuggestedServersModeClicked()

}
