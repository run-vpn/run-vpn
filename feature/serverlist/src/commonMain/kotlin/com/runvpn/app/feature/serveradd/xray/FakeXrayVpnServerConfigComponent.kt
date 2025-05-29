package com.runvpn.app.feature.serveradd.xray

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.servers.domain.entities.VlessNetworkType

class FakeXrayVpnServerConfigComponent : XrayVpnServerConfigComponent {
    override val state: Value<XrayVpnServerConfigComponent.State>
        get() = MutableValue(
            XrayVpnServerConfigComponent.State(
                host = "",
                port = "",
                uuid = "",
                networkType = VlessNetworkType.TCP,
                publicKey = null,
                shortId = null,
                sni = "",
                security = null,
                flow = null,
                networkPrimaryParam = null,
                networkSecondaryParam = null,
                isHostError = false,
                isPortError = false,
                isUuidError = false
            )
        )

    override fun onHostChanged(host: String) {
        TODO("Not yet implemented")
    }

    override fun onPortChanged(port: String) {
        TODO("Not yet implemented")
    }

    override fun onUUidChanged(uuid: String) {
        TODO("Not yet implemented")
    }

    override fun onNetworkTypeChanged(networkType: String) {
        TODO("Not yet implemented")
    }

    override fun onPublicKeyChanged(publicKey: String) {
        TODO("Not yet implemented")
    }

    override fun onSecurityChanged(security: String?) {
        TODO("Not yet implemented")
    }

    override fun onFlowChanged(flow: String?) {
        TODO("Not yet implemented")
    }

    override fun onShortIdChanged(shortId: String) {
        TODO("Not yet implemented")
    }

    override fun onNetworkPrimaryParamChanged(networkPrimaryParam: String) {
        TODO("Not yet implemented")
    }

    override fun onNetworkSecondaryParamChanged(networkSecondaryParam: String) {
        TODO("Not yet implemented")
    }

    override fun onSniChanged(sni: String) {
        TODO("Not yet implemented")
    }

    override fun onPasteClick() {
        TODO("Not yet implemented")
    }

    override fun onQrRead(config: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getServerConfig(): CustomServerDto? {
        return null
    }
}
