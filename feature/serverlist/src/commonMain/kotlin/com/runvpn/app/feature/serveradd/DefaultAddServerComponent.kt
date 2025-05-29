package com.runvpn.app.feature.serveradd

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.runvpn.app.core.common.UriManager
import com.runvpn.app.core.common.UrlConstants
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.servers.data.dto.toDomainServerForTest
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.usecases.SetCurrentServerUseCase
import com.runvpn.app.feature.serveradd.ikev2.Ikev2ConfigComponent
import com.runvpn.app.feature.serveradd.openvpn.OpenVpnServerConfigComponent
import com.runvpn.app.feature.serveradd.oversocks.OverSocksConfigComponent
import com.runvpn.app.feature.serveradd.wireguard.WireGuardConfigComponent
import com.runvpn.app.feature.serveradd.xray.XrayVpnServerConfigComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.createCoroutineScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

internal class DefaultAddServerComponent(
    private val componentContext: ComponentContext,
    private val decomposeComponentFactory: DecomposeComponentFactory,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val serversRepository: ServersRepository,
    private val connectToVpnUseCase: ConnectToVpnUseCase,
    private val connectionManager: VpnConnectionManager,
    private val setCurrentServerUseCase: SetCurrentServerUseCase,
    private val uriManager: UriManager,
    private val serverToEditId: String?,
    private val output: (AddServerComponent.Output) -> Unit,
) : AddServerComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private var server: Server? = null

    companion object {
        private const val MAX_LENGTH_SERVER_NAME = 24
    }

    private val _state = MutableValue(
        AddServerComponent.State(
            name = "",
            isPublic = false,
            showNameError = false,
            protocol = null,
            connectionStatus = null,
            showTestConnectionResult = false,
            buttonAddEnabled = false,
            isInEditMode = false,
        )
    )

    private var _configComponent = SlotNavigation<ChildConfig>()

    private fun handleOpenVpnOutput(output: OpenVpnServerConfigComponent.Output) {
        if (output is OpenVpnServerConfigComponent.Output.ConfigResult) {
            _state.value = state.value.copy(buttonAddEnabled = output.enableButtons)
        }
    }

    private fun handleXrayOutput(output: XrayVpnServerConfigComponent.Output) {
        if (output is XrayVpnServerConfigComponent.Output.ConfigResult) {
            _state.value = state.value.copy(buttonAddEnabled = output.isConfigValid)
        }
    }

    private fun handleWireGuardOutput(output: WireGuardConfigComponent.Output) {
        if (output is WireGuardConfigComponent.Output.ConfigResult) {
            _state.value = state.value.copy(buttonAddEnabled = output.isConfigValid)
        }
    }

    private fun handleOverSocksOutput(output: OverSocksConfigComponent.Output) {
        if (output is OverSocksConfigComponent.Output.ConfigResult) {
            _state.value = state.value.copy(buttonAddEnabled = output.isConfigValid)
        }
    }

    private fun handleIkev2Output(output: Ikev2ConfigComponent.Output) {
        if (output is Ikev2ConfigComponent.Output.ConfigResult) {
            _state.value = state.value.copy(buttonAddEnabled = output.isConfigValid)
        }
    }

    override val configComponent: Value<ChildSlot<*, CustomServerConfigComponent>> = childSlot(
        source = _configComponent,
        handleBackButton = false,
        serializer = ChildConfig.serializer()
    ) { configuration: ChildConfig, context: ComponentContext ->
        when (configuration) {
            is ChildConfig.OpenVpnConfig -> decomposeComponentFactory.createOpenVpnAddServerComponent(
                context,
                configuration.server,
                ::handleOpenVpnOutput
            )

            is ChildConfig.Xray -> {
                decomposeComponentFactory.createXrayServerConfigComponent(
                    context,
                    configuration.server,
                    ::handleXrayOutput
                )
            }

            is ChildConfig.WireGuard -> {
                decomposeComponentFactory.createWireGuardConfigComponent(
                    context,
                    configuration.server,
                    ::handleWireGuardOutput
                )
            }

            is ChildConfig.OverSocks -> {
                decomposeComponentFactory.createOverSocksConfigComponent(
                    context,
                    configuration.server,
                    ::handleOverSocksOutput
                )
            }

            is ChildConfig.Ikev2 -> {
                decomposeComponentFactory.createIkev2ConfigComponent(
                    context,
                    configuration.server,
                    ::handleIkev2Output
                )
            }
        }
    }


    override val state: Value<AddServerComponent.State> = _state


    private val backCallback = BackCallback { onBackClick() }

    init {
        backHandler.register(backCallback)
        backCallback.isEnabled = true

        Logger.d("Server id is: $serverToEditId")

        lifecycle.doOnCreate {
            coroutineScope.launch {
                if (serverToEditId.isNullOrEmpty()) return@launch

                server = serversRepository.getById(serverToEditId)
                server?.let {
                    _state.value = state.value.copy(
                        name = it.name ?: "",
                        isPublic = it.isPublic,
                        protocol = it.protocol,
                        isInEditMode = true
                    )
                    onProtocolChange(it.protocol.name)
                }

            }
        }

        lifecycle.doOnDestroy {
            if (state.value.showTestConnectionResult) {
                connectionManager.disconnect()
            }
        }
    }

    override fun onProtocolChange(protocol: String) {
        val protocolEnum = ConnectionProtocol.valueOf(protocol)
        _state.value = state.value.copy(protocol = protocolEnum)

        when (protocolEnum) {
            ConnectionProtocol.OPENVPN -> {
                _configComponent.activate(ChildConfig.OpenVpnConfig(server))
            }

            ConnectionProtocol.XRAY -> {
                _configComponent.activate(ChildConfig.Xray(server))
            }

            ConnectionProtocol.IKEV2 -> {
                _configComponent.activate(ChildConfig.Ikev2(server))
            }

            ConnectionProtocol.WIREGUARD -> {
                _configComponent.activate(ChildConfig.WireGuard(server))
            }

            ConnectionProtocol.OVERSOCKS -> {
                _configComponent.activate(ChildConfig.OverSocks(server))
            }

            ConnectionProtocol.UNDEFINED -> {
                //ignore
            }
        }
    }

    override fun onFaqClick(protocol: ConnectionProtocol) {
        when (protocol) {
            ConnectionProtocol.OPENVPN -> {
                uriManager.openUri(UrlConstants.OPENVPN_SERVER_DOCS)
            }

            ConnectionProtocol.XRAY -> {
                uriManager.openUri(UrlConstants.XRAY_SERVER_DOCS)
            }

            ConnectionProtocol.IKEV2 -> {
                uriManager.openUri(UrlConstants.IKEV2_SERVER_DOCS)
            }

            ConnectionProtocol.WIREGUARD -> {
                uriManager.openUri(UrlConstants.WIREGUARD_SERVER_DOCS)
            }

            ConnectionProtocol.OVERSOCKS -> {
                uriManager.openUri(UrlConstants.SOCKS5_SERVER_DOCS)
            }

            ConnectionProtocol.UNDEFINED -> {
                //ignore
            }
        }
    }

    override fun onNameChange(name: String) {
        if (name.length < MAX_LENGTH_SERVER_NAME) {
            _state.value =
                state.value.copy(name = name, showNameError = false)
            enableAddButton()
        }
    }


    override fun onServerAddClick() {
        _state.value = state.value.copy(showNameError = false)

        if (state.value.name.isBlank()) {
            _state.value = state.value.copy(showNameError = true)
            return
        }

        coroutineScope.launch {
            val server = configComponent.value.child?.instance
                ?.getServerConfig()?.copy(name = state.value.name, isPublic = state.value.isPublic)
            checkNotNull(server)
            if (serverToEditId == null) {
                serversRepository.addCustomServer(server)
            } else {
                serversRepository.editCustomServer(serverToEditId, server)
            }
            onServerAdded()
        }
    }

    override fun onIsPublicChange(isEnable: Boolean) {
        _state.value = state.value.copy(isPublic = isEnable)
        enableAddButton()
    }

    override fun onConnectClick() {
        coroutineScope.launch {
            val server = configComponent.value.child?.instance?.getServerConfig()
            checkNotNull(server)

            connectToVpnUseCase(server.toDomainServerForTest())
            connectionManager.connectionStatus.collect {
                when (it) {
                    is ConnectionStatus.Connecting -> {
                        _state.value = state.value.copy(
                            showTestConnectionResult = true,
                            connectionStatus = it
                        )
                    }

                    is ConnectionStatus.Disconnected -> {
                        _state.value = state.value.copy(
                            showTestConnectionResult = true
                        )
                    }

                    else -> {
                        _state.value = state.value.copy(connectionStatus = it)
                        if (it is ConnectionStatus.Connected) {
                            connectionManager.disconnect()
                        }
                    }
                }
            }
        }
    }

    override fun onBackClick() {
        output.invoke(AddServerComponent.Output.OnBack)
    }

    private fun onServerAdded() {
        output.invoke(AddServerComponent.Output.OnServerAdded)
    }

    private fun enableAddButton() {
        coroutineScope.launch {
            val server = configComponent.value.child?.instance?.getServerConfig()
            if (server != null) {
                _state.value = state.value.copy(buttonAddEnabled = true)
            }
        }
    }


    @Serializable
    sealed class ChildConfig {
        @Serializable
        data class OpenVpnConfig(val server: Server?) : ChildConfig()

        @Serializable
        data class Xray(val server: Server?) : ChildConfig()

        @Serializable
        data class WireGuard(val server: Server?) : ChildConfig()

        @Serializable
        data class OverSocks(val server: Server?) : ChildConfig()

        @Serializable
        data class Ikev2(val server: Server?) : ChildConfig()
    }

}
