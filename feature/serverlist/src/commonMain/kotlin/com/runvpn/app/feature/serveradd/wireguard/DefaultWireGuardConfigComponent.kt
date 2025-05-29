package com.runvpn.app.feature.serveradd.wireguard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.connection.models.WireGuardConfig
import com.runvpn.app.data.connection.models.WireGuardPeer
import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.usecases.CreateCustomServerUseCase
import com.runvpn.app.data.servers.utils.CustomConfigFields
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class DefaultWireGuardConfigComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService,
    private val createCustomServerUseCase: CreateCustomServerUseCase,
    private val serverToEdit: Server? = null,
    private val output: (WireGuardConfigComponent.Output) -> Unit
) : WireGuardConfigComponent, ComponentContext by componentContext {


    companion object {
        const val WIREGUARD_FILE_EXTENSION = "conf"
    }

    private val _state = MutableValue(
        WireGuardConfigComponent.State(
            ipAddress = serverToEdit?.config?.get(CustomConfigFields.WIREGUARD_FIELD_ADDRESS)
                ?: "",
            privateKey = serverToEdit?.config?.get(CustomConfigFields.WIREGUARD_FIELD_PRIVATE_KEY)
                ?: "",
            publicKey = serverToEdit?.config?.get(CustomConfigFields.WIREGUARD_FIELD_PUBLIC_KEY)
                ?: "",
            dnsServers = serverToEdit?.config?.get(CustomConfigFields.WIREGUARD_FIELD_DNS_SERVERS)
                ?: "",
            port = serverToEdit?.config?.get(CustomConfigFields.WIREGUARD_FIELD_PORT) ?: "",
            mtu = serverToEdit?.config?.get(CustomConfigFields.WIREGUARD_FIELD_MTU) ?: "",
            peers = listOf()
        )
    )

    init {
        serverToEdit?.config?.get(CustomConfigFields.WIREGUARD_FIELD_PEERS)?.let {
            val wgPeers = Json.decodeFromString<List<WireGuardPeer>>(it)
            _state.value = state.value.copy(peers = wgPeers)
        }
    }

    override val state: Value<WireGuardConfigComponent.State>
        get() = _state

    override fun onPrivateKeyChange(privateKey: String) {
        _state.value = state.value.copy(privateKey = privateKey)
        validateConfig()
    }

    override fun onPublicKeyChange(publicKey: String) {
        _state.value = state.value.copy(publicKey = publicKey)
        validateConfig()
    }

    override fun onIpAddressChange(ipAddress: String) {
        _state.value = state.value.copy(ipAddress = ipAddress)
        validateConfig()
    }

    override fun onDnsServersChange(dnsServers: String) {
        _state.value = state.value.copy(dnsServers = dnsServers)
        validateConfig()
    }

    override fun onPortChange(port: String) {
        _state.value = state.value.copy(port = port)
        validateConfig()
    }

    override fun onMtuChange(mtu: String) {
        _state.value = state.value.copy(mtu = mtu)
        validateConfig()
    }

    override fun onPeerPublicKeyChange(index: Int, publicKey: String) {
        val peers = state.value.peers.toMutableList()
        val peer = peers[index].copy(publicKey = publicKey)
        peers[index] = peer
        _state.value = state.value.copy(peers = peers)
        validateConfig()
    }

    override fun onPeerSharedKeyChange(index: Int, sharedKey: String) {
        val peers = state.value.peers.toMutableList()
        val peer = peers[index].copy(preSharedKey = sharedKey)
        peers[index] = peer
        _state.value = state.value.copy(peers = peers)
        validateConfig()
    }

    override fun onPeerEndpointChange(index: Int, endpoint: String) {
        val peers = state.value.peers.toMutableList()
        val peer = peers[index].copy(endpoint = endpoint)
        peers[index] = peer
        _state.value = state.value.copy(peers = peers)
        validateConfig()
    }

    override fun onAddPeer(wgConfig: WireGuardPeer) {
        val peers = state.value.peers.toMutableList()
        peers.add(wgConfig)
        _state.value = state.value.copy(peers = peers)
    }


    override fun onDeletePeer(index: Int) {
        val peers = state.value.peers.toMutableList()
        peers.removeAt(index)
        _state.value = state.value.copy(peers = peers)
        validateConfig()
    }

    override fun onLoadWgConfig(wgConfig: WireGuardConfig?) {
        if (wgConfig == null) {
            messageService.showMessage(AppMessage.UnsupportedFormat())
            return
        }
        _state.value = state.value.copy(
            ipAddress = wgConfig.address,
            privateKey = wgConfig.privateKey,
            publicKey = wgConfig.publicKey ?: "",
            dnsServers = wgConfig.dnsServers ?: "",
            port = wgConfig.port ?: "",
            mtu = wgConfig.mtu ?: "",
            peers = wgConfig.peers
        )
        validateConfig()
    }

    private fun validateConfig() {
        val isValid =
            state.value.ipAddress.isNotEmpty() &&
                    state.value.privateKey.isNotEmpty() &&
                    state.value.peers.isNotEmpty() &&
                    state.value.peers.firstOrNull()?.endpoint.isNullOrEmpty().not()

        output(WireGuardConfigComponent.Output.ConfigResult(isValid))
    }

    override suspend fun getServerConfig(): CustomServerDto? {
        if (state.value.ipAddress.isEmpty() || state.value.peers.isEmpty()) {
            return null
        }

        val jsonPeers = Json.encodeToJsonElement(state.value.peers)

        val config = mapOf(
            Pair(CustomConfigFields.WIREGUARD_FIELD_ADDRESS, state.value.ipAddress),
            Pair(CustomConfigFields.WIREGUARD_FIELD_PRIVATE_KEY, state.value.privateKey),
            Pair(CustomConfigFields.WIREGUARD_FIELD_PUBLIC_KEY, state.value.publicKey),
            Pair(CustomConfigFields.WIREGUARD_FIELD_DNS_SERVERS, state.value.dnsServers),
            Pair(CustomConfigFields.WIREGUARD_FIELD_PORT, state.value.port),
            Pair(CustomConfigFields.WIREGUARD_FIELD_MTU, state.value.mtu),
            Pair(CustomConfigFields.WIREGUARD_FIELD_PEERS, jsonPeers.toString())
        )

        return createCustomServerUseCase(
            host = state.value.ipAddress,
            protocol = ConnectionProtocol.WIREGUARD,
            config = config
        )
    }
}
