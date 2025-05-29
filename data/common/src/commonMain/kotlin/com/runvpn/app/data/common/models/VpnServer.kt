package com.runvpn.app.data.common.models

data class VpnServer(
    val uuid: String,
    val host: String,
    val protocol: ConnectionProtocol,
    val config: Map<String, String?>? = null
)
