package com.runvpn.app.data.connection.models


data class XrayVpnConfig(
    val appUuid: String,
    val host: String,
    val publicKey: String,
    val shortId: String,
    val sni: String,
    val ssPort: Int,
    val ssServerKey: String,
    val ssUserKey: String?,
    val configFields: Map<String, String?>?
)
