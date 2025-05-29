package com.runvpn.app.data.servers.domain.usecases

import com.runvpn.app.data.servers.domain.ServersRepository

class SetServerFavoriteUseCase(private val serversRepository: ServersRepository) {
    suspend operator fun invoke(uuid: String, isFavourite: Boolean) {
        serversRepository.setServerFavorite(uuid, isFavourite)
    }

}
