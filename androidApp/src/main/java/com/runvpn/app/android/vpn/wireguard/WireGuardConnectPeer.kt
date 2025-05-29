package com.runvpn.app.android.vpn.wireguard

import android.os.Parcelable
import com.runvpn.app.data.connection.models.WireGuardPeer
import com.wireguard.config.Peer
import kotlinx.parcelize.Parcelize


@Parcelize
data class WireGuardConnectPeer(
    val publicKey: String,
    val preSharedKey: String,
    val endpoint: String
) : Parcelable

fun WireGuardConnectPeer.toWireGuardConfigPeer(allowedIps: String): Peer {
    val builder = Peer.Builder()
        .parseEndpoint(endpoint)
        .parseAllowedIPs(allowedIps)  //split tunneling of IP

    if (publicKey.isNotEmpty()) builder.parsePublicKey(publicKey)
    if (preSharedKey.isNotEmpty()) builder.parsePreSharedKey(preSharedKey)

    return builder.build()
}

fun WireGuardPeer.toWireGuardConnectPeer() = WireGuardConnectPeer(
    publicKey = publicKey,
    preSharedKey = preSharedKey,
    endpoint = endpoint
)


