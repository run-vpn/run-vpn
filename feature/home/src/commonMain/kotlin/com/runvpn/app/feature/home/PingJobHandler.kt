package com.runvpn.app.feature.home

import com.runvpn.app.core.common.Ping
import com.runvpn.app.core.common.PingHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

/**
 * This is just workaround for working with TEA architecture in
 * HomeFeature (HomeComponent). Because, Feature don't support stopping
 * Effects now.
 */
class PingJobHandler(private val pingHelper: PingHelper) {

    companion object {
        private const val DEFAULT_PORT = 443
        const val DEFAULT_PERIOD_MILLIS = 30_000L
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var job: Job? = null

    private val _ping: MutableStateFlow<Ping> = MutableStateFlow(Ping.PING_ERROR)
    val pingFlow: StateFlow<Ping> = _ping

    fun startPing(ip: String) {
        if (job != null) {
            job?.cancel()
            job = null
        }

        job = flow {
            while (true) {
                emit(pingHelper.ping("https://$ip:$DEFAULT_PORT"))
                delay(DEFAULT_PERIOD_MILLIS)
            }
        }.map {
            _ping.value = it
        }.launchIn(coroutineScope)
    }

    fun stopPing() {
        job?.cancel()
        job = null
    }
}
