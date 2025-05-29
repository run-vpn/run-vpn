package com.runvpn.app.data.connection.domain

import com.runvpn.app.core.common.domain.usecases.CheckIPv4ValidUseCase
import com.runvpn.app.core.common.domain.usecases.CheckIPv6ValidUseCase
import com.runvpn.app.data.connection.data.defaultDnsServers

class AddDnsServerUseCase(
    private val checkIPv4ValidUseCase: CheckIPv4ValidUseCase,
    private val checkIPv6ValidUseCase: CheckIPv6ValidUseCase,
    private val dnsServersRepository: DnsServersRepository
) {

    suspend operator fun invoke(dnsServer: DnsServer): CreateDnsServerResult {
        if (dnsServer.name.isEmpty()) {
            return CreateDnsServerResult.ErrorEmptyName
        } else if (dnsServer.primaryIp.isEmpty()) {
            return CreateDnsServerResult.ErrorEmptyIp
        } else if (!checkIpValid(dnsServer.primaryIp)) {
            return CreateDnsServerResult.ErrorIpInvalid
        }

        val allDns = defaultDnsServers + dnsServersRepository
            .getAllDnsServers()
            .getOrDefault(listOf())

        if (allDns.firstOrNull { it.primaryIp == dnsServer.primaryIp } != null) {
            return CreateDnsServerResult.ErrorAlreadyCreated
        }

        return CreateDnsServerResult.Success(
            dnsServersRepository.createDnsServer(dnsServer).getOrThrow()
        )
    }

    private fun checkIpValid(ip: String) = checkIPv4ValidUseCase(ip) || checkIPv6ValidUseCase(ip)
}

sealed interface CreateDnsServerResult {
    data class Success(val createdServer: DnsServer) : CreateDnsServerResult
    data object ErrorEmptyName : CreateDnsServerResult
    data object ErrorEmptyIp : CreateDnsServerResult
    data object ErrorIpInvalid : CreateDnsServerResult
    data object ErrorAlreadyCreated : CreateDnsServerResult
}
