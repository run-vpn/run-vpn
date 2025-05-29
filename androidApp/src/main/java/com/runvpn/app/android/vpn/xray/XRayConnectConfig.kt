package com.runvpn.app.android.vpn.xray

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class XRayConnectConfig(
    val typ: String,
    val serverIp: String,
    val serverPort: String,
    val appUUID: String,
    val publicKey: String,
    val shortId: String,
    val sni: String,
    val serverKey: String,
    val userKey: String,
    var customConfigs: Map<String, String?>? = null
) : Parcelable
