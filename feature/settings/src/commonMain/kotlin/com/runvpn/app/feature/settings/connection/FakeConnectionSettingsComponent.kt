package com.runvpn.app.feature.settings.connection

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeConnectionSettingsComponent : ConnectionSettingsComponent {
    override val state: Value<ConnectionSettingsComponent.State>
        get() = MutableValue(ConnectionSettingsComponent.State(allowLanConnections = false))

    override fun onAllowLanConnectionsToggle(isEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSplitTunnellingClick() {
        TODO("Not yet implemented")
    }

    override fun onDnsServersClick() {
        TODO("Not yet implemented")
    }
}
