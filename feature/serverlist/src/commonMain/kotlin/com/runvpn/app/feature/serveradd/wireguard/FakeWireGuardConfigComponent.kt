package com.runvpn.app.feature.serveradd.wireguard

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.models.WireGuardConfig
import com.runvpn.app.data.connection.models.WireGuardPeer
import com.runvpn.app.data.servers.data.dto.CustomServerDto

class FakeWireGuardConfigComponent : WireGuardConfigComponent {
    override val state: Value<WireGuardConfigComponent.State>
        get() = MutableValue(
            WireGuardConfigComponent.State(
                ipAddress = "192.168.6.148/32",
                privateKey = "SNfNRmXWACarlZGW2v3o5e9Or2YmL0SIZMooQWHCF0s=",
                publicKey = "0pHhSg3i0tX/ebHzTWV6IeZtN88pEjA3KF7fMm9yWEQ=",
                dnsServers = "1.1.1.1, 8.8.8.8",
                port = "",
                mtu = "",
                peers = mutableListOf(
                    WireGuardPeer(
                        publicKey = "rhCdRqMu8xO/Wyb8OeGltY+wqAvbAWh9hUkmy7nQzQs=",
                        preSharedKey = "",
                        endpoint = "fi2.vpnjantit.com:1024"
                    )
                )
            )
        )

    override fun onPrivateKeyChange(privateKey: String) {
        TODO("Not yet implemented")
    }

    override fun onPublicKeyChange(publicKey: String) {
        TODO("Not yet implemented")
    }

    override fun onIpAddressChange(ipAddress: String) {
        TODO("Not yet implemented")
    }

    override fun onDnsServersChange(dnsServers: String) {
        TODO("Not yet implemented")
    }

    override fun onPortChange(port: String) {
        TODO("Not yet implemented")
    }

    override fun onMtuChange(mtu: String) {
        TODO("Not yet implemented")
    }

    override fun onPeerPublicKeyChange(index: Int, publicKey: String) {
        TODO("Not yet implemented")
    }

    override fun onPeerSharedKeyChange(index: Int, sharedKey: String) {
        TODO("Not yet implemented")
    }

    override fun onPeerEndpointChange(index: Int, endpoint: String) {
        TODO("Not yet implemented")
    }

    override fun onAddPeer(wgConfig: WireGuardPeer) {
        TODO("Not yet implemented")
    }

    override fun onDeletePeer(index: Int) {
        TODO("Not yet implemented")
    }

    override fun onLoadWgConfig(wgConfig: WireGuardConfig?) {
        TODO("Not yet implemented")
    }

    override suspend fun getServerConfig(): CustomServerDto? {
        TODO("Not yet implemented")
    }
}
