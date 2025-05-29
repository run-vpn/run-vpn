package com.runvpn.app.data.connection.domain

class DeleteDnsServerUseCase(
    private val dnsServersRepository: DnsServersRepository
) {

    suspend operator fun invoke(server: DnsServer) = runCatching {
        dnsServersRepository.deleteDnsServer(server)
    }
}
