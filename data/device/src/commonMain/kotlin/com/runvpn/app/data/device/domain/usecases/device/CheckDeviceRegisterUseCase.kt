package com.runvpn.app.data.device.domain.usecases.device

import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class CheckDeviceRegisterUseCase(
    private val appSettingsRepository: AppSettingsRepository
) {
    operator fun invoke(): Boolean {
        return with(appSettingsRepository) {
            !appToken.isNullOrEmpty() && !deviceUuid.isNullOrEmpty()
        }
    }
}
