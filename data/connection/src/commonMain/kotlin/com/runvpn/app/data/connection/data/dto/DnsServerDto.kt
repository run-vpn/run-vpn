package com.runvpn.app.data.connection.data.dto

import com.runvpn.app.data.connection.domain.DnsServer
import kotlinx.serialization.Serializable

@Serializable
data class DnsServerDto(
    val id: Long,
    val name: String,
    val primaryIp: String,
    val secondaryIp: String? = null,
    val website: String?,
    val source: DnsServerSource,
    val isEncrypted: Boolean,
    val isStoreLogs: Boolean,
    val isSecure: Boolean,
    val isBlockingAdultContent: Boolean,
    val isBlockingAds: Boolean,
    val description: String? = null,
) {
    val isMine = source == DnsServerSource.MINE
}

fun DnsServerDto.toDomain() = DnsServer(
    id = this.id,
    name = this.name,
    primaryIp = this.primaryIp,
    secondaryIp = this.secondaryIp,
    website = this.website,
    isEncrypted = this.isEncrypted,
    isStoreLogs = this.isStoreLogs,
    isSecure = this.isSecure,
    isBlockingAdultContent = this.isBlockingAdultContent,
    isBlockingAds = this.isBlockingAds
)
