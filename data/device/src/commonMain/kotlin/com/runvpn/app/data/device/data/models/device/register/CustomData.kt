package com.runvpn.app.data.device.data.models.device.register

import kotlinx.serialization.Serializable

@Serializable
data class CustomData( //todo migrate to map<string, string>
    val networkOperator: String,
    val screenDensityDpi: Int,
    val screenHeightPx: Int,
    val screenWidthPx: Int
)
