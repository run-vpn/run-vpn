package com.runvpn.app.android.vpn.xray

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.runvpn.app.data.connection.VpnConnection
import com.runvpn.app.data.connection.models.XrayVpnConfig
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository


class XrayVpnConnectionImpl(
    private val appContext: Context,
    private val appSettingsRepository: AppSettingsRepository,
    private val xrayVpnConfig: XrayVpnConfig,
) : VpnConnection {

    companion object {
        private const val ConnectionType = "vless"
        private const val XRAY_DEFAULT_SERVER_PORT = "443"
    }

    override fun connect() {
        val dnsServerIp = checkNotNull(appSettingsRepository.selectedDnsServerIP)

        val intent = XRayService.makeIntentForConnect(
            context = appContext,
            config = XRayConnectConfig(
                typ = ConnectionType,
                serverIp = xrayVpnConfig.host,
                serverPort = xrayVpnConfig.ssPort.toString(),
                // hardcoded because vpn-servers does not know about new-backend app UUID's
                appUUID = xrayVpnConfig.appUuid,//xrayVpnConfig.appUuid,
                publicKey = xrayVpnConfig.publicKey,
                shortId = xrayVpnConfig.shortId,
                sni = xrayVpnConfig.sni,
                serverKey = xrayVpnConfig.ssServerKey,
                userKey = xrayVpnConfig.ssUserKey ?: "",
                customConfigs = xrayVpnConfig.configFields
            ),
            splitMode = appSettingsRepository.splitMode,
            allowLanConnections = appSettingsRepository.allowLanConnection,
            dnsServerIP = dnsServerIp,
            timeToShutDown = appSettingsRepository.timeToShutdown
        )

        ContextCompat.startForegroundService(appContext, intent)
    }

    override fun disconnect() {
        ContextCompat.startForegroundService(
            appContext,
            XRayService.makeIntentForDisconnect(appContext)
        )
    }

    override fun pause() {
        val intent = Intent(appContext, XRayService::class.java).apply {
            action = XRayService.ACTION_PAUSE
        }

        ContextCompat.startForegroundService(appContext, intent)
    }


}

