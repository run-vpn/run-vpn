package com.runvpn.app.data.servers.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ServersResponse(
    val items: List<RemoteServer>,
    val defaultConfig: DefaultConfigsResponse
)

@Serializable
data class DefaultConfigsResponse(
    val xray: XrayConfig
)
