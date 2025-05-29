package com.runvpn.app.android.utils

object VpnConstants {
    const val DNS_SERVER_1 = "1.1.1.1"

    const val assets = "assets"
    const val dnsServers_1 = "1.1.1.1"
    const val PRIVATE_VLAN4_CLIENT = "26.26.26.1"
    const val PRIVATE_VLAN4_ROUTER = "26.26.26.2"

    const val loglevel = "--loglevel"
    const val enable_udprelay = "--enable-udprelay"
    const val sock_path = "sock_path"
    const val sock_pathParam = "--sock-path"
    const val tunmtuParam = "--tunmtu"
    const val netif_ipaddr = "--netif-ipaddr"
    const val netif_netmask = "--netif-netmask"
    const val socks_server_addr = "--socks-server-addr"

    const val notice = "notice"
    const val netmask_ip = "255.255.255.252"
    const val server_addr_ = "127.0.0.1:"
    const val TUN2SOCKS = "libtun2socks.so"
    const val PORT_SOCKS = "10808"

    const val VPN_MTU = 1500

    const val OVERSOCKS_MTU = 8500
    const val CONF_FILE_NAME = "tproxy.conf"
    const val OVERSOCKS_LOCAL_IP = "198.18.0.1"
    const val OVERSOCKS_LOCAL_IP_PREFIX = 32

    const val DEFAULT_ALLOWED_IP_PREFIX_4 = "0.0.0.0/0"
    const val DEFAULT_ALLOWED_IP_PREFIX_6 = "::/0"
    const val DEFAULT_ALLOWED_IP_PREFIX_6_NO_LAN = "2000::/3"

}

