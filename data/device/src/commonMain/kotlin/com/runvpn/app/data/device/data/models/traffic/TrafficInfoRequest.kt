package com.runvpn.app.data.device.data.models.traffic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrafficInfoRequest(
    @SerialName("device_ids")
    val deviceIds: List<String>
)
