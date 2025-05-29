package com.runvpn.app.data.servers.data.dto

import com.runvpn.app.data.settings.models.LocalXrayConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XrayConfig(
    @SerialName("public_key")
    val publicKey: String,
    @SerialName("short_id")
    val shortId: String,
    val sni: String,
    @SerialName("ss_port")
    val ssPort: Int,
    @SerialName("ss_server_key")
    val ssServerKey: String,
    @SerialName("ss_user_key")
    val ssUserKey: String?
)


fun XrayConfig.toLocalXray() = LocalXrayConfig(
    publicKey = publicKey,
    shortId = shortId,
    sni = sni,
    ssPort = ssPort,
    ssServerKey = ssServerKey,
    ssUserKey = ssUserKey
)
