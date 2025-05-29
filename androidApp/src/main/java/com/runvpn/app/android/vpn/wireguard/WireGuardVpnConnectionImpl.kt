package com.runvpn.app.android.vpn.wireguard

import android.content.Context
import androidx.core.content.ContextCompat
import com.runvpn.app.data.connection.VpnConnection
import com.runvpn.app.data.connection.models.WireGuardConfig
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class WireGuardVpnConnectionImpl(
    private val context: Context,
    private val appSettingsRepository: AppSettingsRepository,
    private val wireGuardConfig: WireGuardConfig
) : VpnConnection {

    override fun connect() {
        val dnsServerIp = checkNotNull(appSettingsRepository.selectedDnsServerIP)

        val intent = WireGuardService.makeIntentForConnect(
            context = context,
            config = WireGuardConnectConfig(
                ip = wireGuardConfig.address,
                privateKey = wireGuardConfig.privateKey,
                publicKey = wireGuardConfig.publicKey,
                dnsServers = wireGuardConfig.dnsServers,
                peers = wireGuardConfig.peers.map { it.toWireGuardConnectPeer() }
            ),
            splitMode = appSettingsRepository.splitMode,
            dnsServerIp = dnsServerIp,
            allowLanConnection = appSettingsRepository.allowLanConnection,
            timeToShutDown = appSettingsRepository.timeToShutdown
        )

        ContextCompat.startForegroundService(context, intent)
    }

    override fun disconnect() {
        ContextCompat.startForegroundService(
            context,
            WireGuardService.makeIntentForDisconnect(context)
        )
    }

    override fun pause() {
        ContextCompat.startForegroundService(
            context,
            WireGuardService.makeIntentForPause(context)
        )
    }
}
