package com.runvpn.app.data.device.domain.usecases.device

import com.runvpn.app.data.device.domain.DeviceRepository

class ChangeDeviceNameUseCase(
    private val deviceRepository: DeviceRepository
) {

    suspend operator fun invoke(deviceUuid: String, newName: String) = runCatching {
        deviceRepository.changeDeviceName(deviceUuid, newName)
    }
}
