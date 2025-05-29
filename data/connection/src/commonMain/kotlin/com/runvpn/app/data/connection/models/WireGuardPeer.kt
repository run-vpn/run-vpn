package com.runvpn.app.data.connection.models

import kotlinx.serialization.Serializable

@Serializable
data class WireGuardPeer(
    val publicKey: String,
    val preSharedKey: String,
    val endpoint: String
)
