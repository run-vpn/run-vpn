package com.runvpn.app.feature.settings.connection

import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

interface ConnectionSettingsComponent {

    data class State(
        val allowLanConnections: Boolean
    )

    val state: Value<State>

    fun onAllowLanConnectionsToggle(isEnabled: Boolean)
    fun onSplitTunnellingClick()
    fun onDnsServersClick()

    @Serializable
    sealed interface Output {

        @Serializable
        data object OnSplitTunnellingScreenRequested : Output

        @Serializable
        data object OnDnsServersScreenRequested : Output
    }

}
