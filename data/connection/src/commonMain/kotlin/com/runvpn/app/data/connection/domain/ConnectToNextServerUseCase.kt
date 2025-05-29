package com.runvpn.app.data.connection.domain

import co.touchlab.kermit.Logger
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.common.models.ServerSource
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.servers.domain.usecases.SetCurrentServerUseCase
import com.runvpn.app.tea.utils.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConnectToNextServerUseCase(
    private val connectToVpnUseCase: ConnectToVpnUseCase,
    private val setCurrentServerUseCase: SetCurrentServerUseCase,
    private val serversRepository: ServersRepository,
    private val vpnConnectionManager: VpnConnectionManager
) {

    companion object {
        private const val DEFAULT_RECONNECT_TIMEOUT = 4 * 1000L

        private val logger = Logger.withTag("ConnectToNextServerUseCase")
    }

    private val failedServers = mutableSetOf<String?>()

    private var allServers = listOf<Server>()

    private var coroutineScope: CoroutineScope? = null

    private val timer = Timer.countdown(0L)

    private val _currentTime = MutableStateFlow(0L)
    val currentTime: Flow<Long> = _currentTime


    private var timerJob: Job? = null
    private var connectionStatusJob: Job? = null

    private fun connectToNextVPN() {
        val remoteServers = this.allServers
            .filter { it.source == ServerSource.SERVICE && !failedServers.contains(it.country) }

        logger.d("Chosen servers: ${remoteServers.map { "${it.host} ${it.country}" }}")

        if (remoteServers.isNotEmpty()) {
            val nextServer = remoteServers[0]
            failedServers.add(nextServer.country)
            setCurrentServerUseCase(nextServer.uuid)
            connectToVpnUseCase(nextServer)
        }
    }

    suspend operator fun invoke(currentServer: Server) {
        reset()
        coroutineScope = CoroutineScope(Dispatchers.Default)

        connectToVpnUseCase(currentServer)

        if (currentServer.source != ServerSource.SERVICE) {
            return
        }

        failedServers.add(currentServer.country)

        allServers = serversRepository.allServers.value

        timerJob = coroutineScope?.launch {
            timer.currentMillisFlow.collectLatest {
                timerJob?.ensureActive()
                _currentTime.emit(it / 1000L)
                if (it / 1000L == 1L) {
                    logger.d("Connecting to next server")
                    connectToNextVPN()
                }
            }
        }

        /** Искуственная задержка, для избежания преждевременного сброса переподключения.
         * когда статус VPN is Connected, из экрана списка серверов нажать на нерабочий сервер.
         * статус отлавливается как Connected и переподключение отменяется.
         * наблюдается в протоколе Xray.
         * из главного экрана такой проблемы нету,
         * так как при вызове этого же кода есть задерка (~100ms) между вызовом reset() из начала invoke()
         * и инициализацией слушателя статуса VPN. (строки 65 и 97)*/
        delay(300)

        connectionStatusJob = coroutineScope?.launch {
            vpnConnectionManager.connectionStatus.collect {
                connectionStatusJob?.ensureActive()
                when (it) {
                    is ConnectionStatus.Error -> {
                        timer.startTimer(this, DEFAULT_RECONNECT_TIMEOUT)
                    }

                    is ConnectionStatus.Connected -> {
                        reset()
                    }

                    else -> {
                        timer.stopTimer()
                    }
                }
            }
        }
    }

    fun reset() {
        failedServers.clear()

        timerJob?.cancel()
        timerJob = null

        connectionStatusJob?.cancel()
        connectionStatusJob = null

        coroutineScope?.cancel()
        coroutineScope = null
    }
}
