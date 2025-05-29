package com.runvpn.app.feature.serveradd.openvpn

import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.serveradd.CustomServerConfigComponent

interface OpenVpnServerConfigComponent : CustomServerConfigComponent {

    enum class ConfigError {
        CONFIG_INVALID,
        READ_ERROR
    }

    enum class OpenVpnImportMode {
        URL, FILE
    }

    data class State(
        val configFileName: String,
        val username: String,
        val password: String,
        val ovpnUrlImport: String,
        val host: String,
        val ovpnImportMode: OpenVpnImportMode,
        val isServerAuthRequested: Boolean,
        val showHostError: ConfigError?,
        val showUserPassError: Boolean,
        val showUrlError: Boolean,
        val isInEditMode: Boolean
    )

    val state: Value<State>

    fun onImportModeChange(importMode: OpenVpnImportMode)
    fun onLoginChange(username: String)
    fun onPasswordChange(password: String)
    fun onImportUrlChange(url: String)
    fun onReadConfigFromFile(ovpnConfig: String?)
    fun onConfigFileNameLoaded(fileName: String)

    sealed interface Output {
        data class ConfigResult(val enableButtons: Boolean = false) : Output
    }

}
