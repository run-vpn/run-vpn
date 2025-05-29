package com.runvpn.app.data.servers.domain.entities

data class ImportedOverSocksConfig(
    val host: String,
    val port: String,
    val username: String,
    val password: String
)
