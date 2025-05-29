package com.runvpn.app.feature.settings.common

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

class FakeCommonSettingsComponent : CommonSettingsComponent {
    override val state: Value<CommonSettingsComponent.State>
        get() = MutableValue(
            CommonSettingsComponent.State(
                autoConnect = false,
                tactileFeedback = false,
                autoDisconnect = false,
                connectOnNetworkEnabled = false,
                reconnectToNextServer = false,
                suggestedServersMode = SuggestedServersMode.DEFAULT,
                runAfterDeviceStart = false
            )
        )
    override val childSlot: Value<ChildSlot<*, SimpleDialogComponent>>
        get() = MutableValue(ChildSlot<Nothing, Nothing>(null))

    override fun onStartupToggled(isEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSuggestedServersModeChanged(mode: SuggestedServersMode) {
        TODO("Not yet implemented")
    }

    override fun onReconnectToNextServerToggled(isEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onConnectOnNetworkEnabledToggled(isEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onAutoDisconnectToggled(isEnabled: Boolean, timeToShutdown: Long) {
        TODO("Not yet implemented")
    }

    override fun onTactileFeedbackToggled(isEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onAutoConnectToggled(isEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onChangeSuggestedServersModeClicked() {
        TODO("Not yet implemented")
    }
}
