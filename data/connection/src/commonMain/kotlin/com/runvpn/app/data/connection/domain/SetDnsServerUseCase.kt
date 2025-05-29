package com.runvpn.app.data.connection.domain

import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class SetDnsServerUseCase(
    private val appSettingsRepository: AppSettingsRepository
) {

    operator fun invoke(server: DnsServer) {
        appSettingsRepository.selectedDnsServerId = server.id
        appSettingsRepository.selectedDnsServerIP = server.primaryIp
    }
}
