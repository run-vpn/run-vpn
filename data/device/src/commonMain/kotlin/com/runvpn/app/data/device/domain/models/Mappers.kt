package com.runvpn.app.data.device.domain.models

import com.runvpn.app.data.device.data.models.device.DeviceDto
import com.runvpn.app.data.device.data.models.device.DeviceApplication
import com.runvpn.app.data.device.domain.models.device.Device

internal fun DeviceDto.toDomain(): Device {
    return Device(
        uuid = this.uuid,
        application = this.application.toDomain(),
        software = this.software,
        hardware = this.hardware,
        activity = this.activity,
        latestSubscriptionUuid = this.latestSubscriptionUuid,
        name = this.name
    )
}

internal fun DeviceApplication.toDomain(): DeviceApplication {
    return DeviceApplication(
        code = this.code,
        source = this.source,
        versionName = this.versionName
    )
}
