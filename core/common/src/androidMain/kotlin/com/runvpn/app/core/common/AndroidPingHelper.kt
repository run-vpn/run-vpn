package com.runvpn.app.core.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import java.net.InetAddress
import java.net.Socket
import java.net.URL


class AndroidPingHelper : PingHelper {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override suspend fun ping(ip: String): Ping {
        return coroutineScope.async {
            val url = URL(ip)
            try {
                val start = System.currentTimeMillis()
                val hostAddress: String = InetAddress.getByName(url.getHost()).hostAddress
                val dnsResolved = System.currentTimeMillis()
                val socket = Socket(hostAddress, url.port)
                socket.close()
                val probeFinish = System.currentTimeMillis()
                val dns = (dnsResolved - start).toInt()
                val result = (probeFinish - dnsResolved).toInt()

                return@async Ping(result)
            } catch (ex: Exception) {
                return@async Ping(-1)
            }
        }.await()
    }
}
