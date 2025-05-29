package com.runvpn.app.data.connection

import com.arkivanov.decompose.value.Value
import com.runvpn.app.tea.message.domain.NullWrapper

interface ConnectionStatisticsManager {
    val stats: Value<NullWrapper<ConnectionStats>>

    fun startListen(startTime: Long = 0L, timerType: ConnectionStatsTimerType)
    fun stopListen()

    data class ConnectionStats(
        val totalDownloaded: Long,
        val totalUploaded: Long,
        val downloadSpeedPerSecond: Long,
        val uploadSpeedPerSecond: Long,
        val connectionTime: Long,
        val connectionStatsTimerType: ConnectionStatsTimerType
    )

    enum class ConnectionStatsTimerType {
        Countdown,
        Stopwatch
    }

}

