package com.runvpn.app.data.connection.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetDnsServersResponse(
    val items: List<DnsServerDto>
)
