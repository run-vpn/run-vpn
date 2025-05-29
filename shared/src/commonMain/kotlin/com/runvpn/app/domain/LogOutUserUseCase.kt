package com.runvpn.app.domain

import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.domain.AuthorizationRepository
import com.runvpn.app.data.device.domain.usecases.device.RegisterDeviceUseCase
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class LogOutUserUseCase(
    private val authorizationRepository: AuthorizationRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val registerDeviceUseCase: RegisterDeviceUseCase,
    private val deviceInfo: DeviceInfo,
) {


    suspend operator fun invoke() = runCatching {
        if (appSettingsRepository.appToken == appSettingsRepository.appAnonymousToken) return@runCatching
        authorizationRepository.logout()
        appSettingsRepository.appToken = appSettingsRepository.appAnonymousToken

        if (appSettingsRepository.appToken.isNullOrEmpty()) {
            registerDeviceUseCase(deviceInfo)
        }
    }

}
