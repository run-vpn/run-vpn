package com.runvpn.app.data.connection.domain

import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.common.models.toVpnServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ConnectToVpnUseCase(
    private val vpnConnectionManager: VpnConnectionManager,
    private val serversRepository: ServersRepository
) {

    private var job: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(server: Server) {
        job?.cancel()
        val vpnServer = server.toVpnServer()
        val connectionStatus = vpnConnectionManager.connectionStatus.value

        if (connectionStatus is ConnectionStatus.Connected || connectionStatus is ConnectionStatus.Connecting) {
            vpnConnectionManager.reconnect(vpnServer)
        } else {
            vpnConnectionManager.connect(vpnServer)
        }

        job = coroutineScope.launch {
            vpnConnectionManager.connectionStatus.collectLatest {
                when (it) {
                    is ConnectionStatus.Connected -> {
                        serversRepository.setLastConnectionTime(
                            server.uuid,
                            Clock.System.now().toEpochMilliseconds()
                        )
                        job?.cancel()
                    }

                    else -> {}
                }
            }
        }
    }

}

