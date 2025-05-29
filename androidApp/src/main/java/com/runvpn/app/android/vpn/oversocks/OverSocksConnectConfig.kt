package com.runvpn.app.android.vpn.oversocks


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OverSocksConnectConfig(
    val host: String,
    val port: String,
    val dnsV4: String?,
    val udpOverTcp: String,
    val userName: String?,
    val password: String?
) : Parcelable
