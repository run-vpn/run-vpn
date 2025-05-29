package com.runvpn.app.domain

import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.connection.data.cloudflareDnsServer
import com.runvpn.app.data.connection.domain.SetDnsServerUseCase
import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class InitApplicationDataUseCase(
    private val appSettingsRepository: AppSettingsRepository,
    private val serversRepository: ServersRepository,
    private val setDnsServerUseCase: SetDnsServerUseCase
) {

    suspend operator fun invoke() {
        serversRepository.initServers()
        if (appSettingsRepository.selectedDnsServerId == null) {
            setDnsServerUseCase(cloudflareDnsServer)
        }

    }
}
