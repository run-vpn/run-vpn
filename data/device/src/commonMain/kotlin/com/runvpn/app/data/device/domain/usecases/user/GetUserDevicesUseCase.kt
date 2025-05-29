package com.runvpn.app.data.device.domain.usecases.user

import com.runvpn.app.data.device.domain.DeviceRepository

class GetUserDevicesUseCase(
    private val deviceRepository: DeviceRepository
) {

    suspend operator fun invoke() = runCatching {
        val result = deviceRepository.getUserDevices()
        return@runCatching result.getOrThrow()
    }
}
