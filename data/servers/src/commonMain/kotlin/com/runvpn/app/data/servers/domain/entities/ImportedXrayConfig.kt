package com.runvpn.app.data.servers.domain.entities

data class ImportedXrayConfig(
    val subProtocol: String,
    val user: String,
    val host: String,
    val port: String,
    val flow: VlessFlow?,
    val security: VlessSecurity?,
    val encryption: String?,
    val profileName: String,
    val paramNetworkType: VlessNetworkType,
    val paramPublicKey: String?,
    val paramShortId: String?,
    val sni: String?,
    val networkPrimaryParam: String?,
    val networkSecondaryParam: String?
)
