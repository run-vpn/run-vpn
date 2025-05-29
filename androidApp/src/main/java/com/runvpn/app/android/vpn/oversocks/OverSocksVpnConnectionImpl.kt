package com.runvpn.app.android.vpn.oversocks

import android.content.Context
import androidx.core.content.ContextCompat
import com.runvpn.app.data.connection.VpnConnection
import com.runvpn.app.data.connection.models.OverSocksConfig
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class OverSocksVpnConnectionImpl(
    private val context: Context,
    private val appSettingsRepository: AppSettingsRepository,
    private val overSocksConfig: OverSocksConfig
) : VpnConnection {
    override fun connect() {
        val dnsServerIp = checkNotNull(appSettingsRepository.selectedDnsServerIP)

        val intent = OverSocksVpnService.makeIntentForConnect(
            context = context,
            config = OverSocksConnectConfig(
                host = overSocksConfig.host,
                port = overSocksConfig.port,
                dnsV4 = overSocksConfig.dnsV4,
                udpOverTcp = overSocksConfig.udpOverTcp,
                userName = overSocksConfig.userName,
                password = overSocksConfig.password
            ),
            splitMode = appSettingsRepository.splitMode,
            allowLanConnection = appSettingsRepository.allowLanConnection,
            dnsServerIp = dnsServerIp,
            timeToShutDown = appSettingsRepository.timeToShutdown
        )

        ContextCompat.startForegroundService(context, intent)
    }

    override fun disconnect() {
        ContextCompat.startForegroundService(
            context,
            OverSocksVpnService.makeIntentForDisconnect(context)
        )
    }

    override fun pause() {
        ContextCompat.startForegroundService(
            context,
            OverSocksVpnService.makeIntentForPause(context)
        )
    }
}
