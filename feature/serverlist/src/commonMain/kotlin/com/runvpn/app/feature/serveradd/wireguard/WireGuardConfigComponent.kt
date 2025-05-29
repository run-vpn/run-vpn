package com.runvpn.app.feature.serveradd.wireguard

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.models.WireGuardConfig
import com.runvpn.app.data.connection.models.WireGuardPeer
import com.runvpn.app.feature.serveradd.CustomServerConfigComponent

interface WireGuardConfigComponent : CustomServerConfigComponent {

    data class State(
        val privateKey: String,
        val publicKey: String,
        val ipAddress: String,
        val dnsServers: String,
        val port: String,
        val mtu: String,
        val peers: List<WireGuardPeer>
    )


    val state: Value<State>

    fun onPrivateKeyChange(privateKey: String)
    fun onPublicKeyChange(publicKey: String)
    fun onIpAddressChange(ipAddress: String)
    fun onDnsServersChange(dnsServers: String)
    fun onPortChange(port: String)
    fun onMtuChange(mtu: String)


    fun onPeerPublicKeyChange(index: Int, publicKey: String)
    fun onPeerSharedKeyChange(index: Int, sharedKey: String)
    fun onPeerEndpointChange(index: Int, endpoint: String)
    fun onAddPeer(wgConfig: WireGuardPeer = WireGuardPeer("", "", ""))
    fun onDeletePeer(index: Int)

    fun onLoadWgConfig(wgConfig: WireGuardConfig?)

    sealed interface Output {
        data class ConfigResult(val isConfigValid: Boolean = false) : Output
    }

}
