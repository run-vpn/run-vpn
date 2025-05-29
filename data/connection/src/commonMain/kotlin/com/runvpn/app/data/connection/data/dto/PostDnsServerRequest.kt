package com.runvpn.app.data.connection.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostDnsServerRequest(
    val name: String,
    val primaryIp: String,
    val secondaryIp: String?
)
