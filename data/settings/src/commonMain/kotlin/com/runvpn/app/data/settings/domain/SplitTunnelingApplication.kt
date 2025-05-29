package com.runvpn.app.data.settings.domain

import kotlinx.serialization.Serializable

@Serializable
data class SplitTunnelingApplication(
    val packageName: String,
    val name: String,
    val isSystem: Boolean
)
