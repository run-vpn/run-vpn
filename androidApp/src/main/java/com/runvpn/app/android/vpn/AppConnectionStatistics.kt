package com.runvpn.app.android.vpn

import android.net.TrafficStats
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.ConnectionStatisticsManager
import com.runvpn.app.tea.message.domain.NullWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

class AppConnectionStatistics : ConnectionStatisticsManager {

    companion object {
        private const val DEFAULT_LISTEN_DELAY = 1000L
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _stats: MutableValue<NullWrapper<ConnectionStatisticsManager.ConnectionStats>> =
        MutableValue(
            NullWrapper(null)
        )

    override val stats: Value<NullWrapper<ConnectionStatisticsManager.ConnectionStats>> = _stats

    private var listenJob: Job? = null

    override fun startListen(
        startTime: Long,
        timerType: ConnectionStatisticsManager.ConnectionStatsTimerType
    ) {
        listenJob?.cancel()

        listenJob = flow {
            val firstDownloaded = TrafficStats.getTotalRxBytes()
            val firstUploaded = TrafficStats.getTotalTxBytes()

            var lastDownloaded = 0L
            var lastUploaded = 0L

            var currentSeconds = startTime

            while (true) {
                val tDownloaded = TrafficStats.getTotalRxBytes()
                val tUploaded = TrafficStats.getTotalTxBytes()

                val totalDownloaded = tDownloaded - firstDownloaded
                val totalUploaded = tUploaded - firstUploaded

                val downloadSpeed = if (lastDownloaded == 0L) 0L else tDownloaded - lastDownloaded
                val uploadSpeed = if (lastUploaded == 0L) 0L else tUploaded - lastUploaded

                lastDownloaded = tDownloaded
                lastUploaded = tUploaded

                if (timerType == ConnectionStatisticsManager.ConnectionStatsTimerType.Countdown) {
                    currentSeconds -= 1000L
                } else {
                    currentSeconds += 1000L
                }

                emit(
                    ConnectionStatisticsManager.ConnectionStats(
                        totalDownloaded = totalDownloaded,
                        totalUploaded = totalUploaded,
                        downloadSpeedPerSecond = downloadSpeed,
                        uploadSpeedPerSecond = uploadSpeed,
                        connectionTime = currentSeconds / 1000L,
                        connectionStatsTimerType = timerType
                    )
                )

                delay(DEFAULT_LISTEN_DELAY)
            }
        }
            .map { _stats.value = NullWrapper(it) }
            .flowOn(Dispatchers.Default)
            .launchIn(coroutineScope)
    }

    override fun stopListen() {
        listenJob?.cancel()

        _stats.value = NullWrapper(null)
    }
}

