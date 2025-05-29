package com.runvpn.app.android.vpn.wireguard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WireGuardConnectConfig(
    val ip: String,
    val privateKey: String,
    val publicKey: String?,
    val dnsServers: String?,
    val peers: List<WireGuardConnectPeer>
) : Parcelable
