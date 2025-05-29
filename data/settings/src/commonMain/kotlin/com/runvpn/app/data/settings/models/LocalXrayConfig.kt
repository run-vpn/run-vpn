package com.runvpn.app.data.settings.models

import kotlinx.serialization.Serializable

@Serializable
data class LocalXrayConfig (
    val publicKey : String,
    val shortId : String,
    val sni : String,
    val ssPort : Int,
    val ssServerKey : String,
    val ssUserKey : String?
)
