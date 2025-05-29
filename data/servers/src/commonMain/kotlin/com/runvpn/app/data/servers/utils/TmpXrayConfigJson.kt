package com.runvpn.app.data.servers.utils

object TmpXrayConfigJson {

    fun createXrayConfig(
        uuid: String,
        ip: String,
        port: Int,
        networkType: String,
        security: String,
        sni: String,
        flow: String,
        shortId: String,
        publicKey: String,
        networkPrimaryParam: String,
        networkSecondaryParam: String
    ) =
        "{\"dns\":{\"hosts\":{\"domain:googleapis.cn\":\"googleapis.com\"},\"servers\":[\"1.1.1.1\"]},\"inbounds\":[{\"listen\":\"127.0.0.1\",\"port\":10808,\"protocol\":\"socks\",\"settings\":{\"auth\":\"noauth\",\"udp\":true,\"userLevel\":8},\"sniffing\":{\"enabled\":false},\"tag\":\"socks\"},{\"listen\":\"127.0.0.1\",\"port\":10809,\"protocol\":\"http\",\"settings\":{\"userLevel\":8},\"tag\":\"http\"}],\"log\":{\"loglevel\":\"warning\"},\"outbounds\":[{\"mux\":{\"concurrency\":-1,\"enabled\":false,\"xudpConcurrency\":8,\"xudpProxyUDP443\":\"\"},\"protocol\":\"vless\",\"settings\":{\"vnext\":[{\"address\":\"$ip\",\"port\":$port,\"users\":[{\"encryption\":\"none\",\"flow\":\"$flow\",\"id\":\"$uuid\",\"level\":8,\"security\":\"auto\"}]}]},\"streamSettings\":{\"network\":\"$networkType\",\"security\":\"$security\",\"wsSettings\":{\"headers\":{\"Host\":\"$networkPrimaryParam\"},\"path\":\"$networkSecondaryParam\"},\"realitySettings\":{\"shortId\":\"$shortId\",\"fingerprint\":\"chrome\",\"publicKey\":\"$publicKey\",\"spiderX\":\"\",\"show\":false,\"serverName\":\"$sni\"}},\"tag\":\"proxy\"},{\"protocol\":\"freedom\",\"settings\":{},\"tag\":\"direct\"},{\"protocol\":\"blackhole\",\"settings\":{\"response\":{\"type\":\"http\"}},\"tag\":\"block\"}],\"routing\":{\"domainStrategy\":\"IPIfNonMatch\",\"rules\":[{\"ip\":[\"1.1.1.1\"],\"outboundTag\":\"proxy\",\"port\":\"53\",\"type\":\"field\"}]}}"
}
