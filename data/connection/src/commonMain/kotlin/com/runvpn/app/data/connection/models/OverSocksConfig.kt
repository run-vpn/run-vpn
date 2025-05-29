package com.runvpn.app.data.connection.models

data class OverSocksConfig(
    val host: String,
    val port: String,
    val dnsV4: String?,
    val udpOverTcp: String,
    val userName: String?,
    val password: String?
)
