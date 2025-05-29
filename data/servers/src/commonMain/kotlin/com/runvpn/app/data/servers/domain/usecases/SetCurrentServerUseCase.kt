package com.runvpn.app.data.servers.domain.usecases

import com.runvpn.app.data.servers.domain.ServersRepository

class SetCurrentServerUseCase(
    private val serversRepository: ServersRepository
) {

    operator fun invoke(uuid: String) {
        serversRepository.setCurrentServer(uuid)
    }
}
