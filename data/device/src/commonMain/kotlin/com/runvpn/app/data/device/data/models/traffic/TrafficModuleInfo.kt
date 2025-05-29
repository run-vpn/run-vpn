package com.runvpn.app.data.device.data.models.traffic

import kotlinx.serialization.Serializable

@Serializable
data class TrafficModuleInfo(
    val allDevTotal: TrafficInfo = TrafficInfo(),
    val currDevTotal: TrafficInfo = TrafficInfo(),
    val currDevToday: TrafficInfo = TrafficInfo(),
    val currDevYesterday: TrafficInfo = TrafficInfo()
)
