package com.runvpn.app.data.device.data.models.traffic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrafficInfoDevice(
    @SerialName("device_id")
    val deviceId: String,
    val received: String,
    val transferred: String,
    @SerialName("duration")
    val durationS: Long,
    @SerialName("count")
    val reconnectCount: Long,
) {
    val clientAccumulated = received.toLong() + transferred.toLong()
}
