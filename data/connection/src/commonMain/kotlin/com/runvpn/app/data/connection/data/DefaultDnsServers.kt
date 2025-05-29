package com.runvpn.app.data.connection.data

import com.runvpn.app.data.connection.domain.DnsServer

val googleDnsServer = DnsServer(
    id = 0L,
    name = "Google",
    primaryIp = "8.8.8.8",
    secondaryIp = "8.8.4.4",
    website = "https://developers.google.com/speed/public-dns?hl=ru",
    isEncrypted = false,
    isStoreLogs = true,
    isSecure = true,
    isBlockingAdultContent = false,
    isBlockingAds = false,
    description = "DNS-server from Google"
)

val cloudflareDnsServer = DnsServer(
    id = 0L,
    name = "Cloudflare",
    primaryIp = "1.1.1.1",
    website = "https://www.cloudflare.com/dns",
    isEncrypted = false,
    isStoreLogs = false,
    isSecure = false,
    isBlockingAdultContent = false,
    isBlockingAds = false,
    description = "Public DNS-server from Cloudflare"
)

val cloudflareFamilyNoMalwareDnsServer = DnsServer(
    id = 0L,
    name = "Cloudflare - Malware blocking",
    primaryIp = "1.1.1.2",
    secondaryIp = "1.0.0.2",
    website = "https://www.cloudflare.com/dns",
    isEncrypted = false,
    isStoreLogs = false,
    isSecure = true,
    isBlockingAdultContent = false,
    isBlockingAds = false,
    description = "Public DNS-server from Cloudflare that blocks malware"
)

val cloudflareFamilyNoAdultDnsServer = DnsServer(
    id = 0L,
    name = "Cloudflare - Malware and Adult content blocking",
    primaryIp = "1.1.1.3",
    secondaryIp = "1.0.0.3",
    website = "https://www.cloudflare.com/dns",
    isEncrypted = false,
    isStoreLogs = false,
    isSecure = true,
    isBlockingAdultContent = true,
    isBlockingAds = false,
    description = "Public DNS-server from Cloudflare that blocks malware and adult content"
)

val openDnsServer = DnsServer(
    id = 0L,
    name = "OpenDNS",
    primaryIp = "208.67.222.123",
    secondaryIp = "208.67.220.123",
    website = "https://www.opendns.com",
    isEncrypted = true,
    isSecure = true,
    isBlockingAds = false,
    isBlockingAdultContent = true,
    isStoreLogs = true,
    description = "Public DNS-server from Cisco that blocks malware and fishing"
)

val openDnsNoAdultServer = DnsServer(
    id = 0L,
    name = "OpenDNS with FamilyShield",
    primaryIp = "208.67.220.123",
    secondaryIp = "208.67.222.123",
    website = "https://www.opendns.com",
    isEncrypted = true,
    isSecure = true,
    isBlockingAds = false,
    isBlockingAdultContent = true,
    isStoreLogs = true,
    description = "Public DNS-server from Cisco that blocks malware and adult content"
)

val quad9DnsServer = DnsServer(
    id = 0L,
    name = "Quad9",
    primaryIp = "9.9.9.9",
    secondaryIp = "149.112.112.112",
    website = "https://quad9.net/",
    isEncrypted = true,
    isSecure = true,
    isStoreLogs = false,
    isBlockingAds = false,
    isBlockingAdultContent = false,
    description = "Public DNS-server that blocks bad domain's"
)

val defaultDnsServers: List<DnsServer> = listOf(
    googleDnsServer,
    cloudflareDnsServer,
    cloudflareFamilyNoMalwareDnsServer,
    cloudflareFamilyNoAdultDnsServer,
    openDnsServer,
    openDnsNoAdultServer,
    quad9DnsServer
)
