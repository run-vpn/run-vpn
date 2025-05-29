package com.runvpn.app.data.device.domain.usecases.device

import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class DeviceDeleteUseCase(
    private val deviceRepository: DeviceRepository,
    private val appSettingsRepository: AppSettingsRepository
) {

    suspend operator fun invoke(deviceUuid: String) {
        val token = checkNotNull(appSettingsRepository.appToken)
        deviceRepository.deleteDevice(token, deviceUuid)
    }
}
