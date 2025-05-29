package com.runvpn.app.data.connection

import co.touchlab.kermit.Logger
import com.runvpn.app.core.common.checkNotNullOrEmpty
import com.runvpn.app.data.connection.models.Ikev2Config
import com.runvpn.app.data.connection.models.OpenVpnConfig
import com.runvpn.app.data.connection.models.OverSocksConfig
import com.runvpn.app.data.common.models.VpnServer
import com.runvpn.app.data.connection.models.WireGuardConfig
import com.runvpn.app.data.connection.models.WireGuardPeer
import com.runvpn.app.data.connection.models.XrayVpnConfig
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.servers.utils.CustomConfigFields
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class VpnConnectionManager(
    private val vpnConnectionFactory: VpnConnectionFactory,
    private val appSettingsRepository: AppSettingsRepository,
    private val excludedAppIdsDefaults: ExcludedAppIdsDefaults
) {

    companion object {
        const val CONNECTION_TIMEOUT_MILLIS = 20_000
        private val logger: Logger = Logger.withTag("VpnConnectionManager")
    }

    /**
     * Stream of current connection status
     */
    private val _connectionStatus: MutableStateFlow<ConnectionStatus> =
        MutableStateFlow(ConnectionStatus.Disconnected)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    var currentVpnConnection: VpnConnection? = null
        private set

    val excludedPackageIds: List<AppPackageId>
        get() {
            return excludedAppIdsDefaults.getDefaultExcludedApps() +
                    appSettingsRepository.excludedPackageIds.map {
                        AppPackageId(it.packageName)
                    }
        }

    val splitMode: Int
        get() {
            return appSettingsRepository.splitMode
        }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var reconnectJob: Job? = null
    private var timeoutJob: Job? = null

    private var latestServer: VpnServer? = null

    fun connectLatest() {
        latestServer?.let {
            connect(it)
        }
    }

    /**
     * Connect to VPN.
     */
    @OptIn(FlowPreview::class)
    fun connect(server: VpnServer) {
        Logger.i("Connect VpnServer! $server")
        timeoutJob?.cancel()

        latestServer = server

        if (!connectionStatus.value.isDisconnected()) return

        when (server.protocol) {
            ConnectionProtocol.XRAY -> {
                val xrayVpnConfig = makeXrayConfig(server)
                currentVpnConnection =
                    vpnConnectionFactory.createXrayVpnConnection(xrayVpnConfig)
            }

            ConnectionProtocol.OPENVPN -> {
                val openVpnConfig = makeOpenVpnConfig(server)
                currentVpnConnection = vpnConnectionFactory.createOpenVpnConnection(openVpnConfig)
            }

            ConnectionProtocol.WIREGUARD -> {
                val wireGuardConfig = makeWireGuardConfig(server)
                currentVpnConnection =
                    vpnConnectionFactory.createWireGuardConnection(wireGuardConfig)
            }

            ConnectionProtocol.OVERSOCKS -> {
                val overSocksConfig = makeOverSocksConfig(server)
                currentVpnConnection =
                    vpnConnectionFactory.createOverSocksConnection(overSocksConfig)
            }

            ConnectionProtocol.IKEV2 -> {
                val ikev2Config = makeIkev2Config(server)
                currentVpnConnection = vpnConnectionFactory.createIkev2Connection(ikev2Config)
            }

            ConnectionProtocol.UNDEFINED -> {
                //ignore
            }
        }
        currentVpnConnection?.connect()

        timeoutJob = connectionStatus
            .filter { it is ConnectionStatus.Connected }
            .timeout(CONNECTION_TIMEOUT_MILLIS.toDuration(DurationUnit.MILLISECONDS))
            .catch { throwable ->
                if (connectionStatus.value !is ConnectionStatus.Connecting) {
                    timeoutJob?.cancel()
                    return@catch
                }

                if (throwable is TimeoutCancellationException) {
                    disconnect()
                    setConnectionStatus(ConnectionStatus.Error("Timeout error"))
                    timeoutJob?.cancel()
                }
            }
            .launchIn(coroutineScope)
    }

    private fun makeOverSocksConfig(server: VpnServer): OverSocksConfig {
        val config = requireNotNull(server.config)

        val host = config[CustomConfigFields.OVERSOCKS_FIELD_ADDRESS]
        val port = config[CustomConfigFields.OVERSOCKS_FIELD_PORT]
        val udpOverTcp = config[CustomConfigFields.OVERSOCKS_FIELD_UDP_IN_TCP]

        checkNotNull(host)
        checkNotNull(port)
        checkNotNull(udpOverTcp)

        return OverSocksConfig(
            host = host,
            port = port,
            udpOverTcp = udpOverTcp,
            dnsV4 = config[CustomConfigFields.OVERSOCKS_FIELD_DNS_V4],
            userName = config[CustomConfigFields.OVERSOCKS_FIELD_USERNAME],
            password = config[CustomConfigFields.OVERSOCKS_FIELD_PASSWORD]
        )
    }


    private fun makeXrayConfig(server: VpnServer): XrayVpnConfig {
        val appUUID = checkNotNull(appSettingsRepository.deviceUuid)
//        val xrayConfig = checkNotNull(appSettingsRepository.xrayConfig)
        val host = checkNotNull(server.host)

//        return XrayVpnConfig(
//            appUuid = appUUID,
//            host = host,
//            publicKey = xrayConfig.publicKey,
//            shortId = xrayConfig.shortId,
//            sni = xrayConfig.sni,
//            ssPort = xrayConfig.ssPort,
//            ssServerKey = xrayConfig.ssServerKey,
//            ssUserKey = xrayConfig.ssUserKey,
//            configFields = server.config
//        )

        val port = server.config?.get("port")?.toIntOrNull() ?: 0
        return XrayVpnConfig(
            appUuid = appUUID,
            host = host,
            publicKey ="",
            shortId = "",
            sni = "",
            ssPort = port,
            ssServerKey = "",
            ssUserKey = "",
            configFields = server.config
        )
    }


    private fun makeOpenVpnConfig(server: VpnServer): OpenVpnConfig {
        val config = requireNotNull(server.config)
        return OpenVpnConfig(
            config = config[CustomConfigFields.OVPN_FIELD_CONFIG],
            userName = config[CustomConfigFields.OVPN_FIELD_USERNAME],
            password = config[CustomConfigFields.OVPN_FIELD_PASSWORD]
        )
    }

    private fun makeWireGuardConfig(server: VpnServer): WireGuardConfig {
        val config = requireNotNull(server.config)

        with(config) {
            val peers = this[CustomConfigFields.WIREGUARD_FIELD_PEERS]
            val address = this[CustomConfigFields.WIREGUARD_FIELD_ADDRESS]
            val privateKey = this[CustomConfigFields.WIREGUARD_FIELD_PRIVATE_KEY]

            checkNotNull(peers)
            checkNotNull(address)
            checkNotNull(privateKey)

            val jsonPeers = Json.parseToJsonElement(peers).jsonArray

            val listPeers = mutableListOf<WireGuardPeer>()
            for (jsonElement in jsonPeers) {
                val wgPeed =
                    Json.decodeFromString<WireGuardPeer>(jsonElement.jsonObject.toString())
                listPeers.add(wgPeed)
            }

            return WireGuardConfig(
                address = address,
                privateKey = privateKey,
                publicKey = this[CustomConfigFields.WIREGUARD_FIELD_PUBLIC_KEY],
                dnsServers = this[CustomConfigFields.WIREGUARD_FIELD_DNS_SERVERS],
                port = this[CustomConfigFields.WIREGUARD_FIELD_PORT],
                mtu = this[CustomConfigFields.WIREGUARD_FIELD_MTU],
                peers = listPeers
            )
        }
    }

    private fun makeIkev2Config(server: VpnServer): Ikev2Config {
        val config = requireNotNull(server.config)

        val host = config[CustomConfigFields.IKEV2_FIELD_HOST]
        val username = config[CustomConfigFields.IKEV2_FIELD_USERNAME]
        val password = config[CustomConfigFields.IKEV2_FIELD_PASSWORD]
        val certificateName = config[CustomConfigFields.IKEV2_FIELD_CERTIFICATE_NAME]
        val certificate = config[CustomConfigFields.IKEV2_FIELD_CERTIFICATE]

        checkNotNullOrEmpty(host)
        checkNotNullOrEmpty(username)
        checkNotNullOrEmpty(password)
        checkNotNullOrEmpty(certificateName)
        checkNotNullOrEmpty(certificate)


        return Ikev2Config(
            host = host,
            username = username,
            password = password,
            certificateName = certificateName,
            certificate = certificate
        )
    }

    /**
     * Disconnects from VPN.
     */
    fun disconnect() {
        currentVpnConnection?.disconnect()
    }

    /**
     * Pauses VPN connection. Don't stop the VpnService.
     */
    fun pause() {
        currentVpnConnection?.pause()
    }

    /**
     * Changes connectionStatus field value.
     */
    fun setConnectionStatus(newStatus: ConnectionStatus) {
        if (connectionStatus.value is ConnectionStatus.Error && newStatus is ConnectionStatus.Disconnected) {
            return
        }
        _connectionStatus.value = newStatus
    }


    /**
     * Disconnects from current connection and wait until **connectionStatus** is Disconnected,
     * then do connect to passed server.
     *
     * Doesn't do anything if **connectionStatus** is Disconnected. If so, use [connect] for this
     * purpose.
     *
     * @param server server for new connection.
     */
    fun reconnect(server: VpnServer) {
        if (connectionStatus.value is ConnectionStatus.Disconnected) return

        disconnect()

        reconnectJob = coroutineScope.launch {
            connectionStatus.collect {
                reconnectJob?.ensureActive()
                if (it == ConnectionStatus.Disconnected) {
                    connect(server)
                    reconnectJob?.cancel()
                }
            }
        }
    }

    fun reconnectToLatestServer() {
        latestServer?.let {
            reconnect(it)
        }
    }
}
