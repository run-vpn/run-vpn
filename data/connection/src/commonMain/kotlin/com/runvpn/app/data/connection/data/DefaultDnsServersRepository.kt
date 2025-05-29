package com.runvpn.app.data.connection.data

import com.runvpn.app.data.connection.data.dto.PostDnsServerRequest
import com.runvpn.app.data.connection.data.dto.toDomain
import com.runvpn.app.data.connection.domain.DnsServer
import com.runvpn.app.data.connection.domain.DnsServersRepository
import com.runvpn.app.db.cache.DbDnsServer
import com.runvpn.app.db.cache.DbDnsServerQueries

internal class DefaultDnsServersRepository(
    private val dnsServersApi: DnsServersApi,
    private val dnsServerQueries: DbDnsServerQueries
) : DnsServersRepository {

    override suspend fun getAllDnsServers(): Result<List<DnsServer>> = runCatching {
        val dnsServersRemote = dnsServersApi.getDnsServers().getOrThrow()
        return@runCatching dnsServersRemote.items.filter { it.isMine }.map { it.toDomain() }
    }

    override suspend fun createDnsServer(server: DnsServer) = runCatching {
        val result = dnsServersApi.postDnsServer(
            PostDnsServerRequest(
                name = server.name,
                primaryIp = server.primaryIp,
                secondaryIp = server.secondaryIp
            )
        )

        val id = result.getOrThrow().id

        dnsServerQueries.addServer(
            id = id,
            name = server.name,
            description = server.description,
            ip = server.primaryIp,
            secondaryIP = server.secondaryIp,
            website = server.website,
            isEncrypted = server.isEncrypted,
            isStoreLogs = server.isStoreLogs,
            isSecure = server.isSecure,
            isBlockingAdultContent = server.isBlockingAdultContent,
            isBlockingAds = server.isBlockingAds
        ).executeAsOne().toDomain()
    }

    override suspend fun createMultipleDnsServers(serverList: List<DnsServer>): Result<Unit> =
        runCatching {
            val result = dnsServersApi.getDnsServers().getOrThrow()

            result.items.forEach { server ->
                dnsServerQueries.addServer(
                    id = server.id,
                    name = server.name,
                    description = server.description,
                    ip = server.primaryIp,
                    secondaryIP = server.secondaryIp,
                    website = server.website,
                    isEncrypted = server.isEncrypted,
                    isStoreLogs = server.isStoreLogs,
                    isSecure = server.isSecure,
                    isBlockingAdultContent = server.isBlockingAdultContent,
                    isBlockingAds = server.isBlockingAds
                ).executeAsOne()
            }
        }

    override suspend fun deleteDnsServer(server: DnsServer) = runCatching {
        val result = dnsServersApi.deleteDnsServer(server.id)
        result.getOrThrow()
        dnsServerQueries.deleteServer(server.id)
    }

    override fun clearDnsServers() {
        dnsServerQueries.clear()
    }

    private fun DbDnsServer.toDomain(): DnsServer = DnsServer(
        id = this.id,
        name = this.name,
        primaryIp = this.ip,
        secondaryIp = this.secondaryIP,
        website = this.website,
        isEncrypted = this.isEncrypted,
        isStoreLogs = this.isStoreLogs,
        isSecure = this.isSecure,
        isBlockingAdultContent = this.isBlockingAdultContent,
        isBlockingAds = this.isBlockingAds,
        description = this.description
    )
}
