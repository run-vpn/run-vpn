package com.runvpn.app.android.vpn

import android.content.Context
import com.runvpn.app.android.vpn.ikev2.Ikev2ConnectionImpl
import com.runvpn.app.android.vpn.openvpn.OpenVpnConnectionImpl
import com.runvpn.app.android.vpn.oversocks.OverSocksVpnConnectionImpl
import com.runvpn.app.android.vpn.wireguard.WireGuardVpnConnectionImpl
import com.runvpn.app.android.vpn.xray.XrayVpnConnectionImpl
import com.runvpn.app.data.connection.VpnConnection
import com.runvpn.app.data.connection.VpnConnectionFactory
import com.runvpn.app.data.connection.models.Ikev2Config
import com.runvpn.app.data.connection.models.OpenVpnConfig
import com.runvpn.app.data.connection.models.OverSocksConfig
import com.runvpn.app.data.connection.models.WireGuardConfig
import com.runvpn.app.data.connection.models.XrayVpnConfig
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class VpnConnectionFactoryImpl(
    private val appContext: Context,
    private val appSettingsRepository: AppSettingsRepository
) : VpnConnectionFactory {

    override fun createXrayVpnConnection(xrayVpnConfig: XrayVpnConfig): VpnConnection {
        return XrayVpnConnectionImpl(appContext, appSettingsRepository, xrayVpnConfig)
    }

    override fun createOpenVpnConnection(openVpnConfig: OpenVpnConfig): VpnConnection {
        return OpenVpnConnectionImpl(appContext, appSettingsRepository, openVpnConfig)
    }

    override fun createWireGuardConnection(wireGuardConfig: WireGuardConfig): VpnConnection {
        return WireGuardVpnConnectionImpl(appContext, appSettingsRepository, wireGuardConfig)
    }

    override fun createOverSocksConnection(overSocksConfig: OverSocksConfig): VpnConnection {
        return OverSocksVpnConnectionImpl(appContext, appSettingsRepository, overSocksConfig)
    }

    override fun createIkev2Connection(ikev2Config: Ikev2Config): VpnConnection {
        return Ikev2ConnectionImpl(appContext, appSettingsRepository, ikev2Config)
    }


}
