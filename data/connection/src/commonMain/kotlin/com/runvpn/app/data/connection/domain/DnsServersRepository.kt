package com.runvpn.app.data.connection.domain

interface DnsServersRepository {
    suspend fun getAllDnsServers(): Result<List<DnsServer>>
    suspend fun createDnsServer(server: DnsServer): Result<DnsServer>
    suspend fun createMultipleDnsServers(serverList: List<DnsServer>): Result<Unit>
    suspend fun deleteDnsServer(server: DnsServer): Result<Unit>
    fun clearDnsServers()
}
