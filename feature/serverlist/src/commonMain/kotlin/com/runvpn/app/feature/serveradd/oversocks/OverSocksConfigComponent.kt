package com.runvpn.app.feature.serveradd.oversocks

import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.serveradd.CustomServerConfigComponent

interface OverSocksConfigComponent : CustomServerConfigComponent {


    data class State(
        val host: String,
        val port: String,
        val username: String,
        val password: String,
        val udpOverTcp: Boolean,
    )


    val state: Value<State>


    fun onHostChange(host: String)
    fun onPortChange(port: String)
    fun onUserNameChange(username: String)
    fun onPasswordChange(password: String)
    fun onUdpOverTcpToggled(isEnabled: Boolean)

    fun onPasteClick()

    fun onLoadConfig(config: String)


    sealed interface Output {
        data class ConfigResult(val isConfigValid: Boolean = false) : Output
    }

}
