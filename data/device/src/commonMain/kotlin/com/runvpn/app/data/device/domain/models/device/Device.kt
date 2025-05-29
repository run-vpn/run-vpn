package com.runvpn.app.data.device.domain.models.device

import com.runvpn.app.data.device.data.models.device.Activity
import com.runvpn.app.data.device.data.models.device.DeviceApplication
import com.runvpn.app.data.device.data.models.device.register.Hardware
import com.runvpn.app.data.device.data.models.device.register.Software
import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val uuid: String,
    val application: DeviceApplication,
    val software: Software,
    val hardware: Hardware,
    val activity: Activity?,
    val latestSubscriptionUuid: String?,
    val name: String?
) {
    val fullName: String?
        get() = name ?: hardware.marketName ?: hardware.brand ?: hardware.productName
        ?: hardware.name
}
