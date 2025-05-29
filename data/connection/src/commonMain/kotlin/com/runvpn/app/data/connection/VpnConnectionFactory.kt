package com.runvpn.app.data.connection

import com.runvpn.app.data.connection.models.Ikev2Config
import com.runvpn.app.data.connection.models.OpenVpnConfig
import com.runvpn.app.data.connection.models.OverSocksConfig
import com.runvpn.app.data.connection.models.WireGuardConfig
import com.runvpn.app.data.connection.models.XrayVpnConfig

interface VpnConnectionFactory {
    fun createXrayVpnConnection(xrayVpnConfig: XrayVpnConfig): VpnConnection
    fun createOpenVpnConnection(openVpnConfig: OpenVpnConfig): VpnConnection
    fun createWireGuardConnection(wireGuardConfig: WireGuardConfig): VpnConnection
    fun createOverSocksConnection(overSocksConfig: OverSocksConfig): VpnConnection
    fun createIkev2Connection(ikev2Config: Ikev2Config): VpnConnection
}
