package com.runvpn.app.data.device.domain.usecases.update

import com.runvpn.app.data.device.domain.UpdateRepository

class GetApplicationInfoUseCase(
    private val updateRepository: UpdateRepository
) {

    suspend operator fun invoke(applicationCode: String, source: String) = runCatching {
        return@runCatching updateRepository.getUpdateInfo(applicationCode, source)
    }
}
