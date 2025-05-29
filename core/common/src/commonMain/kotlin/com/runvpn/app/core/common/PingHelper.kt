package com.runvpn.app.core.common

import kotlin.jvm.JvmInline


@JvmInline
value class Ping(val ping: Int) {
    companion object {
        val PING_ERROR = Ping(-1)
    }
}

/**
 * Helper for getting ping remote servers.
 */
interface PingHelper {

    /**
     * ICMP ping
     * @return ping in milliseconds
     */
    suspend fun ping(ip: String): Ping
}

