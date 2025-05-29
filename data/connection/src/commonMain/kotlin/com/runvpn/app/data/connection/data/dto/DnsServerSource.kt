package com.runvpn.app.data.connection.data.dto

import kotlinx.serialization.SerialName

enum class DnsServerSource {
    @SerialName("mine")
    MINE,
    @SerialName("service")
    SERVICE
}
