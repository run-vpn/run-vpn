package com.runvpn.app.data.device.data.models.device

import com.runvpn.app.data.device.data.models.device.register.Hardware
import com.runvpn.app.data.device.data.models.device.register.Software
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDto(
    val uuid: String,
    val application: DeviceApplication,
    val software: Software,
    val hardware: Hardware,
    val activity: Activity?,
    @Deprecated("migrates to latestSubscription: JsonObject")
    val latestSubscriptionUuid: String?,
    val name: String?
) {
    val fullName: String?
        get() = name ?: hardware.marketName ?: hardware.brand ?: hardware.productName
        ?: hardware.name
}
