package com.runvpn.app.data.device.domain.usecases.device

import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val appSettingsRepository: AppSettingsRepository
) {

    suspend operator fun invoke(deviceInfo: DeviceInfo): Result<Unit> = runCatching {
        val result = deviceRepository.registerDevice(deviceInfo).getOrThrow()

        appSettingsRepository.appToken = result.token
        appSettingsRepository.appAnonymousToken = result.token
        appSettingsRepository.deviceUuid = result.deviceUuid
    }
}
