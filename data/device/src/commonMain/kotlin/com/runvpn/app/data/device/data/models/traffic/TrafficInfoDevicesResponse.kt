package com.runvpn.app.data.device.data.models.traffic

import kotlinx.serialization.Serializable

@Serializable
data class TrafficInfoDevicesResponse(
    val data: List<TrafficInfoDevice>
) {
    val clientsAccumulated: Long
        get() = data.sumOf { it.clientAccumulated }

    fun clientAccumulated(id: String): Long {
        return data.sumOf {
            if (it.deviceId != id) {
                0L
            } else {
                it.clientAccumulated
            }
        }
    }
}
