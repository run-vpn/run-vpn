package com.runvpn.app.feature.serveradd.openvpn

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.servers.domain.entities.ConfigValidationResult
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.usecases.CreateCustomServerUseCase
import com.runvpn.app.data.servers.domain.usecases.PrepareOpenVpnConfigUseCase
import com.runvpn.app.data.servers.domain.usecases.ValidateOpenVpnConfigUseCase
import com.runvpn.app.data.servers.utils.CustomConfigFields
import io.ktor.http.Url

class DefaultOpenVpnServerConfigComponent(
    componentContext: ComponentContext,
    private val prepareOpenVpnConfigUseCase: PrepareOpenVpnConfigUseCase,
    private val validateOpenVpnConfigUseCase: ValidateOpenVpnConfigUseCase,
    private val createCustomServerUseCase: CreateCustomServerUseCase,
    private val serverToEdit: Server? = null,
    private val output: (OpenVpnServerConfigComponent.Output) -> Unit
) : OpenVpnServerConfigComponent, ComponentContext by componentContext {

    companion object {
        const val OVPN_FILE_EXTENSION = "ovpn"
    }


    private var ovpnConfig: String =
        serverToEdit?.config?.get(CustomConfigFields.OVPN_FIELD_CONFIG) ?: ""

    private val _state = MutableValue(
        OpenVpnServerConfigComponent.State(
            isInEditMode = serverToEdit != null,
            host = serverToEdit?.host ?: "",
            username = serverToEdit?.config?.get(CustomConfigFields.OVPN_FIELD_USERNAME) ?: "",
            password = serverToEdit?.config?.get(CustomConfigFields.OVPN_FIELD_PASSWORD) ?: "",
            ovpnImportMode = OpenVpnServerConfigComponent.OpenVpnImportMode.FILE,
            configFileName = "",
            ovpnUrlImport = "",
            isServerAuthRequested = false,
            showHostError = null,
            showUserPassError = false,
            showUrlError = false
        )
    )

    override val state: Value<OpenVpnServerConfigComponent.State>
        get() = _state


    override fun onImportModeChange(importMode: OpenVpnServerConfigComponent.OpenVpnImportMode) {
        _state.value = state.value.copy(
            ovpnImportMode = importMode,
            showUserPassError = false,
            showHostError = null,
            showUrlError = false,
            ovpnUrlImport = ""
        )
        ovpnConfig = ""
        sendConfigResultOutput()
    }

    override fun onLoginChange(username: String) {
        _state.value = state.value.copy(username = username, showUserPassError = false)
        sendConfigResultOutput()
    }

    override fun onPasswordChange(password: String) {
        _state.value = state.value.copy(password = password, showUserPassError = false)
        sendConfigResultOutput()
    }

    override fun onImportUrlChange(url: String) {
        _state.value = state.value.copy(ovpnUrlImport = url)
        sendConfigResultOutput()
    }

    override fun onReadConfigFromFile(ovpnConfig: String?) {
        if (ovpnConfig.isNullOrEmpty()) {
            _state.value =
                state.value.copy(showHostError = OpenVpnServerConfigComponent.ConfigError.READ_ERROR)
            return
        }
        this.ovpnConfig = ovpnConfig
        sendConfigResultOutput()
    }

    override fun onConfigFileNameLoaded(fileName: String) {
        _state.value = state.value.copy(
            configFileName = fileName,
            ovpnUrlImport = "",
            showHostError = if (fileName.isEmpty()) {
                OpenVpnServerConfigComponent.ConfigError.CONFIG_INVALID
            } else null
        )
    }

    override suspend fun getServerConfig(): CustomServerDto? {
        when (val result = validateConfig()) {
            is ConfigValidationResult.Success<*> -> {
                _state.value = state.value.copy(host = result.host)

                val config = mapOf(
                    Pair(CustomConfigFields.OVPN_FIELD_CONFIG, result.config.toString()),
                    Pair(CustomConfigFields.OVPN_FIELD_USERNAME, state.value.username),
                    Pair(CustomConfigFields.OVPN_FIELD_PASSWORD, state.value.password)
                )

                return createCustomServerUseCase(
                    host = result.host,
                    protocol = ConnectionProtocol.OPENVPN,
                    config = config
                )
            }

            ConfigValidationResult.AuthRequired -> {
                _state.value = state.value.copy(showUserPassError = true)
            }

            ConfigValidationResult.ConfigInvalid -> {
                _state.value =
                    state.value.copy(
                        showHostError = OpenVpnServerConfigComponent.ConfigError.CONFIG_INVALID,
                        showUrlError = true
                    )
            }
        }

        return null
    }

    private suspend fun validateConfig(): ConfigValidationResult {
        val config = when (state.value.ovpnImportMode) {
            OpenVpnServerConfigComponent.OpenVpnImportMode.FILE -> {
                prepareOpenVpnConfigUseCase(ovpnConfig)
            }

            OpenVpnServerConfigComponent.OpenVpnImportMode.URL -> {
                prepareOpenVpnConfigUseCase(Url(state.value.ovpnUrlImport))
            }
        }
        return validateOpenVpnConfigUseCase(config, state.value.username, state.value.password)
    }

    private fun sendConfigResultOutput() {
        output(
            OpenVpnServerConfigComponent.Output.ConfigResult(
                ovpnConfig.isNotEmpty() || state.value.ovpnUrlImport.isNotEmpty()
            )
        )
    }

}
