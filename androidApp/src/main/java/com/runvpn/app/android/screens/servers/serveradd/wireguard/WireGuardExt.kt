package com.runvpn.app.android.screens.servers.serveradd.wireguard

import com.runvpn.app.data.connection.models.WireGuardConfig
import com.runvpn.app.data.connection.models.WireGuardPeer
import com.wireguard.config.BadConfigException
import com.wireguard.config.Config
import java.io.IOException
import kotlin.jvm.optionals.getOrNull

fun createWgConfig(config: String): WireGuardConfig? {
    try {
        val cfg = Config.parse(config.byteInputStream())
        val privateKey = cfg.`interface`.keyPair.privateKey.toBase64()
        val publicKey = cfg.`interface`.keyPair.publicKey.toBase64()
        val ipAddress =
            cfg.`interface`.addresses.joinToString { "${it.address.hostAddress}/${it.mask}" }
        val dnsServers = cfg.`interface`.dnsServers.joinToString { it.hostAddress ?: "" }

        val port = cfg.`interface`.listenPort.getOrNull()?.toString() ?: ""
        val mtu = cfg.`interface`.mtu.getOrNull()?.toString() ?: ""
        val peers = mutableListOf<WireGuardPeer>()

        cfg.peers.forEach { peer ->
            peers.add(
                WireGuardPeer(
                    publicKey = peer.publicKey.toBase64(),
                    preSharedKey = peer.preSharedKey.getOrNull()?.toBase64() ?: "",
                    endpoint = peer.endpoint.get().toString()
                )
            )

        }

        return WireGuardConfig(
            privateKey = privateKey,
            publicKey = publicKey,
            address = ipAddress,
            dnsServers = dnsServers,
            port = port,
            mtu = mtu,
            peers = peers
        )
    } catch (e: IOException) {
        return null
    } catch (e: BadConfigException) {
        return null
    }
}
