package com.runvpn.app.data.device.domain.models.device

import com.runvpn.app.data.settings.domain.Tariff
import kotlinx.serialization.Serializable

@Serializable
data class DeviceWithTariff(
    val device: Device,
    val tariff: Tariff
)
