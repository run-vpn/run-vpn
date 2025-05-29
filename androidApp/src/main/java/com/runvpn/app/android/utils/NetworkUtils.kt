package com.runvpn.app.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.Inet4Address
import java.net.Inet6Address
import java.util.Vector
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow


object NetworkUtils {

    fun getLocalNetworks(context: Context, ipv6: Boolean = false): Vector<String> {
        val nets = Vector<String>()
        val connectivityManger =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManger.activeNetwork
        val linkProperties = connectivityManger.getLinkProperties(activeNetwork)
        val networkCapabilities = connectivityManger.getNetworkCapabilities(activeNetwork)

        if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            linkProperties?.linkAddresses?.forEach {
                if ((it.address is Inet4Address && !ipv6) ||
                    (it.address is Inet6Address && ipv6)
                ) {
                    nets.add(it.toString())
                }
            }
        }

        return nets
    }

    fun range2CIDRList(startIp: String, endIp: String): List<String> {
        var start: Long = ipToLong(startIp)
        val end: Long = ipToLong(endIp)
        val pairs = ArrayList<String>()
        while (end >= start) {
            var maxsize: Byte = 32
            while (maxsize > 0) {
                val mask: Long = CIDR2MASK.get(maxsize - 1)
                val maskedBase = start and mask
                if (maskedBase != start) {
                    break
                }
                maxsize--
            }
            val x = ln((end - start + 1).toDouble()) / ln(2.0)
            val maxdiff = (32 - floor(x)).toInt().toByte()
            if (maxsize < maxdiff) {
                maxsize = maxdiff
            }
            val ip: String = longToIP(start)
            pairs.add("$ip/$maxsize")
            start = (start + 2.0.pow((32 - maxsize).toDouble())).toLong()
        }


        return pairs
    }


    private val CIDR2MASK = longArrayOf(
        0x00000000, -0x80000000,
        -0x40000000, -0x20000000, -0x10000000, -0x8000000, -0x4000000,
        -0x2000000, -0x1000000, -0x800000, -0x400000, -0x200000,
        -0x100000, -0x80000, -0x40000, -0x20000, -0x10000,
        -0x8000, -0x4000, -0x2000, -0x1000, -0x800,
        -0x400, -0x200, -0x100, -0x80, -0x40,
        -0x20, -0x10, -0x8, -0x4, -0x2,
        -0x1
    )

    private fun ipToLong(strIP: String): Long {
        val ip = LongArray(4)
        val ipSec = strIP.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (k in 0..3) {
            ip[k] = java.lang.Long.valueOf(ipSec[k])
        }
        return (ip[0] shl 24) + (ip[1] shl 16) + (ip[2] shl 8) + ip[3]
    }

    private fun longToIP(longIP: Long): String {
        val sb = StringBuffer("")
        sb.append((longIP ushr 24).toString())
        sb.append(".")
        sb.append((longIP and 0x00FFFFFFL ushr 16).toString())
        sb.append(".")
        sb.append((longIP and 0x0000FFFFL ushr 8).toString())
        sb.append(".")
        sb.append((longIP and 0x000000FFL).toString())
        return sb.toString()
    }
}
