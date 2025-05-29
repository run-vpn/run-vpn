package com.runvpn.app.android.vpn.ikev2

import android.content.Context
import androidx.core.content.ContextCompat
import com.runvpn.app.data.connection.VpnConnection
import com.runvpn.app.data.connection.models.Ikev2Config
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class Ikev2ConnectionImpl(
    private val appContext: Context,
    private val appSettingsRepository: AppSettingsRepository,
    private val ikev2Config: Ikev2Config,
) : VpnConnection {
    override fun connect() {
        val dnsServerIp = checkNotNull(appSettingsRepository.selectedDnsServerIP)
        ContextCompat.startForegroundService(
            appContext,
            Ikev2VpnService.makeIntentForConnect(
                context = appContext,
                config = Ikev2ConnectConfig(
                    host = ikev2Config.host,
                    username = ikev2Config.username,
                    password = ikev2Config.password,
                    certificate = ikev2Config.certificate
                ),
                splitMode = appSettingsRepository.splitMode,
                allowLanConnection = appSettingsRepository.allowLanConnection,
                dnsServerIp = dnsServerIp,
                timeToShutDown = appSettingsRepository.timeToShutdown
            )
        )
    }

    override fun disconnect() {
        ContextCompat.startForegroundService(
            appContext,
            Ikev2VpnService.makeIntentForDisconnect(appContext)
        )
    }

    override fun pause() {
        ContextCompat.startForegroundService(
            appContext,
            Ikev2VpnService.makeIntentForPause(appContext)
        )
    }
}
