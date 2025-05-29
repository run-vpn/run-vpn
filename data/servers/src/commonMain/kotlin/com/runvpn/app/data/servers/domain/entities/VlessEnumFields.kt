package com.runvpn.app.data.servers.domain.entities

enum class VlessNetworkType {
    TCP, KCP, WS, H2, QUIC, GRPC
}


enum class VlessSecurity {
    TLS, REALITY
}


enum class VlessFlow(val value: String) {
    XTLS("xtls-rprx-vision"),
    XTLS_UDP("xtls-rprx-vision-udp443")
}
