package com.runvpn.app.data.servers.utils

object CustomConfigFields {

    const val OVPN_FIELD_CONFIG = "config"
    const val OVPN_FIELD_USERNAME = "username"
    const val OVPN_FIELD_PASSWORD = "password"


    const val XRAY_FIELD_CONFIG = "xrayConfig"
    const val XRAY_FIELD_UUID = "uud"
    const val XRAY_FIELD_HOST = "ip"
    const val XRAY_FIELD_PORT = "port"
    const val XRAY_FIELD_NETWORK_TYPE = "type"
    const val XRAY_FIELD_SECURITY = "security"
    const val XRAY_FIELD_FLOW = "flow"
    const val XRAY_FIELD_ENCRYPTION = "encryption"
    const val XRAY_FIELD_PBK = "pbk"
    const val XRAY_FIELD_SID = "sid"
    const val XRAY_FIELD_SNI = "sni"

    const val XRAY_FIELD_NETWORK_PRIMARY_PARAM = "host"
    const val XRAY_FIELD_NETWORK_SECONDARY_PARAM = "path"


    const val WIREGUARD_FIELD_ADDRESS = "address"
    const val WIREGUARD_FIELD_PRIVATE_KEY = "privateKey"
    const val WIREGUARD_FIELD_PUBLIC_KEY = "publicKey"
    const val WIREGUARD_FIELD_DNS_SERVERS = "dnsServers"
    const val WIREGUARD_FIELD_PORT = "port"
    const val WIREGUARD_FIELD_MTU = "mtu"

    const val WIREGUARD_FIELD_PEERS = "peers"


    const val OVERSOCKS_FIELD_ADDRESS = "address"
    const val OVERSOCKS_FIELD_PORT = "port"
    const val OVERSOCKS_FIELD_USERNAME = "username"
    const val OVERSOCKS_FIELD_PASSWORD = "password"
    const val OVERSOCKS_FIELD_DNS_V4 = "dnsv4"
    const val OVERSOCKS_FIELD_UDP_IN_TCP = "udpInTcp"
    const val OVERSOCKS_PARAM_TCP = "tcp"
    const val OVERSOCKS_PARAM_UDP = "udp"


    const val IKEV2_FIELD_HOST = "host"
    const val IKEV2_FIELD_USERNAME = "username"
    const val IKEV2_FIELD_PASSWORD = "password"
    const val IKEV2_FIELD_CERTIFICATE_NAME = "certificateName"
    const val IKEV2_FIELD_CERTIFICATE = "certificate"

}
