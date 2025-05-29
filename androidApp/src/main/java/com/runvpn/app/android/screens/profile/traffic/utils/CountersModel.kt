package com.runvpn.app.android.screens.profile.traffic.utils


class CountersModel {
    private var lastTime = 0L
    private var lastBytesIn = 0L
    private var lastBytesOut = 0L
    fun getSpeed(currTime: Long, bytesOut: Long, bytesIn: Long): Pair<Long, Long> {
        if (bytesOut == 0L || bytesIn == 0L) reset() // disconnect detected

        val timeDiff = currTime - lastTime
        val speedOut = (1000.0 * (bytesOut - lastBytesOut) / timeDiff).toLong()
        val speedIn = (1000.0 * (bytesIn - lastBytesIn) / timeDiff).toLong()

        lastTime = currTime
        lastBytesOut = bytesOut
        lastBytesIn = bytesIn

        return Pair(speedOut, speedIn)
    }

    fun reset() {
        lastTime = 0L
        lastBytesIn = 0L
        lastBytesOut = 0L
    }
}
