package com.runvpn.app.android.vpn


import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.runvpn.app.SharedSDK
import com.runvpn.app.android.MyApp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.isNotificationsAllowed
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.common.models.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RunVpnTileService : TileService() {

    private var connectionManager: VpnConnectionManager? = null
    private var serverToConnect: Server? = null
    private var connectToVpnUseCase: ConnectToVpnUseCase? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    override fun onCreate() {
        super.onCreate()
        connectionManager = (applicationContext as MyApp).vpnConnectionManager

        with(SharedSDK.koinApplication.koin) {
            serverToConnect = get<ServersRepository>().run {
                currentServer.value
            }
            connectToVpnUseCase = get<ConnectToVpnUseCase>()
        }

        coroutineScope.launch {
            connectionManager?.connectionStatus?.collect {
                updateTile(it)
            }
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        val status = connectionManager?.connectionStatus?.value ?: return
        updateTile(status)
    }

    override fun onClick() {
        if (!this.isNotificationsAllowed()) return
        super.onClick()
        val status = connectionManager?.connectionStatus?.value ?: return
        when (status) {
            is ConnectionStatus.Connected -> connectionManager?.disconnect()

            is ConnectionStatus.Disconnected,
            is ConnectionStatus.Error,
            is ConnectionStatus.Paused -> {
                serverToConnect?.let {
                    connectToVpnUseCase?.invoke(it)
                }
            }

            else -> {
                //ignored
            }
        }
    }

    private fun updateTile(connectionStatus: ConnectionStatus) {
        val tile = qsTile ?: return
        when (connectionStatus) {
            is ConnectionStatus.Connected -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label =
                    serverToConnect?.country ?: serverToConnect?.name ?: serverToConnect?.host
            }

            is ConnectionStatus.Disconnected -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.connect)
            }

            is ConnectionStatus.Error -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = connectionStatus.message
            }

            is ConnectionStatus.Connecting -> {
                tile.state = Tile.STATE_UNAVAILABLE
                tile.label = getString(R.string.vpn_notification_status_connecting)
            }

            is ConnectionStatus.Paused -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.vpn_notification_status_pause)
            }
            is ConnectionStatus.Idle -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.idle)
            }

        }

        tile.updateTile()
    }
}
