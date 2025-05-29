package com.runvpn.app.domain

import com.runvpn.app.data.connection.domain.DnsServersRepository
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class ClearAppCacheUseCase(
    private val appSettingsRepository: AppSettingsRepository,
    private val serversRepository: ServersRepository,
    private val dnsServersRepository: DnsServersRepository
) {


    operator fun invoke() {
        appSettingsRepository.email = null
        dnsServersRepository.clearDnsServers()
        serversRepository.clearServers()
    }

}
