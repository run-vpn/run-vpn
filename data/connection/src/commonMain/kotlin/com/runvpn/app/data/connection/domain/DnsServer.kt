package com.runvpn.app.data.connection.domain

import kotlinx.serialization.Serializable

@Serializable
data class DnsServer(
    val id: Long,
    val name: String,
    val primaryIp: String,
    val secondaryIp: String? = null,
    val website: String?,
    val isEncrypted: Boolean,
    val isStoreLogs: Boolean,
    val isSecure: Boolean,
    val isBlockingAdultContent: Boolean,
    val isBlockingAds: Boolean,
    val description: String? = null,
) {
    companion object {

        /**
         * Creates simple DnsServer with false all boolean fields.
         */
        fun createEmpty(name: String, ip: String): DnsServer = DnsServer(
            id = 0,
            name = name,
            primaryIp = ip,
            website = null,
            isEncrypted = false,
            isStoreLogs = false,
            isSecure = false,
            isBlockingAdultContent = false,
            isBlockingAds = false
        )

        val DEFAULT: DnsServer
            get() = DnsServer(
                id = 0,
                name = "Default (Cloudflare)",
                primaryIp = "1.1.1.1",
                website = "https://1.1.1.1/",
                isEncrypted = false,
                isStoreLogs = false,
                isBlockingAdultContent = false,
                isBlockingAds = false,
                isSecure = false
            )
    }
}
