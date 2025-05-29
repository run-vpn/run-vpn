package com.runvpn.app.feature.serveradd.oversocks

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.ClipboardManager
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.servers.domain.entities.ConfigValidationResult
import com.runvpn.app.data.servers.domain.entities.ImportedOverSocksConfig
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.usecases.CreateCustomServerUseCase
import com.runvpn.app.data.servers.domain.usecases.ExtractSocks5FromUrlUseCase
import com.runvpn.app.data.servers.utils.CustomConfigFields
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage

class DefaultOverSocksConfigComponent(
    componentContext: ComponentContext,
    private val clipboardManager: ClipboardManager,
    private val createCustomServerUseCase: CreateCustomServerUseCase,
    private val extractSocks5FromUrlUseCase: ExtractSocks5FromUrlUseCase,
    private val messageService: MessageService,
    private val serverToEdit: Server? = null,
    private val output: (OverSocksConfigComponent.Output) -> Unit
) : OverSocksConfigComponent, ComponentContext by componentContext {

    private val _state = MutableValue(
        OverSocksConfigComponent.State(
            host = serverToEdit?.config?.get(CustomConfigFields.OVERSOCKS_FIELD_ADDRESS) ?: "",
            port = serverToEdit?.config?.get(CustomConfigFields.OVERSOCKS_FIELD_PORT) ?: "",
            username = serverToEdit?.config?.get(CustomConfigFields.OVERSOCKS_FIELD_USERNAME)
                ?: "",
            password = serverToEdit?.config?.get(CustomConfigFields.OVERSOCKS_FIELD_PASSWORD)
                ?: "",
            udpOverTcp = serverToEdit?.config?.get(CustomConfigFields.OVERSOCKS_FIELD_UDP_IN_TCP) ==
                    CustomConfigFields.OVERSOCKS_PARAM_TCP
        )
    )

    override val state: Value<OverSocksConfigComponent.State>
        get() = _state

    override fun onHostChange(host: String) {
        _state.value = state.value.copy(host = host)
        checkFormValidation()
    }

    override fun onPortChange(port: String) {
        _state.value = state.value.copy(port = port)
        checkFormValidation()
    }

    override fun onUserNameChange(username: String) {
        _state.value = state.value.copy(username = username)
    }

    override fun onPasswordChange(password: String) {
        _state.value = state.value.copy(password = password)
    }

    override fun onUdpOverTcpToggled(isEnabled: Boolean) {
        _state.value = state.value.copy(udpOverTcp = isEnabled)
    }


    override fun onPasteClick() {
        clipboardManager.paste()?.let {
            onLoadConfig(it)
        }
    }

    override fun onLoadConfig(config: String) {
        val result = extractSocks5FromUrlUseCase(config)

        when (result) {
            is ConfigValidationResult.Success<*> -> {
                val conf = result.config as ImportedOverSocksConfig

                _state.value = state.value.copy(
                    host = conf.host,
                    port = conf.port,
                    username = conf.username,
                    password = conf.password
                )
            }

            else -> {
                messageService.showMessage(AppMessage.UnsupportedFormat())
            }
        }
        checkFormValidation()
    }


    override suspend fun getServerConfig(): CustomServerDto? {
        if (state.value.host.isEmpty() || state.value.port.isEmpty()) {
            return null
        }

        val udpOverTcp = if (state.value.udpOverTcp) CustomConfigFields.OVERSOCKS_PARAM_TCP else
            CustomConfigFields.OVERSOCKS_PARAM_UDP

        val config = mapOf(
            Pair(CustomConfigFields.OVERSOCKS_FIELD_ADDRESS, state.value.host),
            Pair(CustomConfigFields.OVERSOCKS_FIELD_PORT, state.value.port),
            Pair(CustomConfigFields.OVERSOCKS_FIELD_USERNAME, state.value.username),
            Pair(CustomConfigFields.OVERSOCKS_FIELD_PASSWORD, state.value.password),
            Pair(CustomConfigFields.OVERSOCKS_FIELD_UDP_IN_TCP, udpOverTcp),
        )

        return createCustomServerUseCase(
            host = state.value.host,
            config = config,
            protocol = ConnectionProtocol.OVERSOCKS
        )
    }

    private fun checkFormValidation() {
        output(
            OverSocksConfigComponent.Output.ConfigResult(
                state.value.host.isNotEmpty() &&
                        state.value.port.isNotEmpty()
            )
        )
    }

}
