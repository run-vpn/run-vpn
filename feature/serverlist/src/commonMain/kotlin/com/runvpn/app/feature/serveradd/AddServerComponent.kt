package com.runvpn.app.feature.serveradd

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.connection.ConnectionStatus

interface AddServerComponent {


    val configComponent: Value<ChildSlot<*, CustomServerConfigComponent>>

    data class State(
        val name: String,
        val isPublic: Boolean,
        val showNameError: Boolean,
        val protocol: ConnectionProtocol?,
        val connectionStatus: ConnectionStatus?,
        val showTestConnectionResult: Boolean,
        val buttonAddEnabled: Boolean,
        val isInEditMode: Boolean,
    )

    val state: Value<State>

    fun onNameChange(name: String)
    fun onProtocolChange(protocol: String)
    fun onFaqClick(protocol: ConnectionProtocol)

    fun onConnectClick()
    fun onServerAddClick()

    fun onIsPublicChange(isEnable: Boolean)


    fun onBackClick()

    sealed interface Output {
        data object OnBack : Output
        data object OnServerAdded : Output
    }
}
