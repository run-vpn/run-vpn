package com.runvpn.app.android.vpn.openvpn

import android.content.Context
import android.net.VpnService
import com.runvpn.app.android.MyApp
import com.runvpn.app.android.R
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnection
import com.runvpn.app.data.connection.models.OpenVpnConfig
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import de.blinkt.openvpn.OpenVPNApikt

class OpenVpnConnectionImpl(
    private val appContext: Context,
    private val appSettingsRepository: AppSettingsRepository,
    private val openVpnConfig: OpenVpnConfig
) : VpnConnection {


    override fun connect() {
        val dnsServerIp = checkNotNull(appSettingsRepository.selectedDnsServerIP)

        VpnService.prepare(appContext)
        (appContext as MyApp).vpnConnectionManager.setConnectionStatus(
            ConnectionStatus.Connecting(appContext.getString(R.string.initializing_ovpn))
        )
        OpenVPNApikt.startVpn(
            appContext,
            openVpnConfig.config,
            "openVpnServer.country",
            openVpnConfig.userName,
            openVpnConfig.password,
            appContext.openVpnNotificationBuilder,
            excludedAppIds = appContext.vpnConnectionManager.excludedPackageIds.map {
                it.packageId
            },
            splitMode = appSettingsRepository.splitMode,
            /** @param allowLanConnection пробрасывается до OpenVPNService.java,
            но не используется, из-за того,
            что данная логика регулируется исходя из параметра в .ovpn конфиг файле*/
            allowLanConnection = appSettingsRepository.allowLanConnection,
            dnsServerIp = dnsServerIp,
            timeToShutdownInMillis = appSettingsRepository.timeToShutdown
        )
    }

    override fun disconnect() {
        OpenVPNApikt.stopVpn(appContext)
    }

    override fun pause() {
        OpenVPNApikt.pauseVpn(appContext)
    }

}


