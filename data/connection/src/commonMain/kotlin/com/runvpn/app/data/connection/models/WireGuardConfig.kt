package com.runvpn.app.data.connection.models

data class WireGuardConfig(
    val address: String,
    val port: String?,
    val dnsServers: String?,
    val mtu: String?,
    val privateKey: String,
    val publicKey: String?,
    val peers: List<WireGuardPeer>
)
