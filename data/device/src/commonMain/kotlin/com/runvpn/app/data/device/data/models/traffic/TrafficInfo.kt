package com.runvpn.app.data.device.data.models.traffic

import kotlinx.serialization.Serializable

@Serializable
data class TrafficInfo(
    var accumBytes: Long = 0,
    var waitModeBytes: Long = 0,
    var usedBytes: Long = 0,
    var onlineTimeMs: Long = 0,
    var reconnectCount: Long = 0
)
