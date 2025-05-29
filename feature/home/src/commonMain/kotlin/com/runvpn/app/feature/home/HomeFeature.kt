package com.runvpn.app.feature.home


import co.touchlab.kermit.Logger
import com.runvpn.app.core.analytics.reports.Report
import com.runvpn.app.core.analytics.reports.ReportService
import com.runvpn.app.core.common.Ping
import com.runvpn.app.core.common.PingHelper
import com.runvpn.app.core.common.VibratorManager
import com.runvpn.app.data.common.domain.usecases.CheckReviewRequiredUseCase
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.connection.ConnectionStatisticsManager
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.domain.ConnectToNextServerUseCase
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.connection.domain.ConnectionStatistics
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.data.device.domain.usecases.device.CheckDeviceRegisterUseCase
import com.runvpn.app.data.device.domain.usecases.device.RegisterDeviceUseCase
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.servers.domain.entities.SuggestedServers
import com.runvpn.app.data.servers.domain.usecases.GetSuggestedServersUseCase
import com.runvpn.app.data.servers.domain.usecases.SetCurrentServerUseCase
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.data.settings.models.AppVersion
import com.runvpn.app.tea.runtime.coroutines.Effect
import com.runvpn.app.tea.runtime.coroutines.Update
import com.runvpn.app.tea.runtime.coroutines.noEffects
import com.runvpn.app.tea.runtime.coroutines.with
import com.runvpn.app.tea.utils.Timer
import com.runvpn.app.tea.utils.asStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

object HomeFeature {

    data class State(
        val suggestedServers: SuggestedServers?,
        val currentServer: Server?,
        val isLoading: Boolean,
        val isDomainReachable: Boolean,
        val isError: Boolean,
        val vpnStatus: ConnectionStatus,
        val vpnStatusHistory: List<String>,
        val connectionTime: Long,
        val connectionErrorTime: Long,
        val connectionStats: ConnectionStatisticsManager.ConnectionStats?,
        val appVersion: AppVersion?,
        val updateStatus: UpdateStatus?,
        val currentPing: Int = 0
    )

    sealed class Message {
        data object Init : Message()

        data class SuggestedServersResponse(val suggestedServers: SuggestedServers) : Message()
        data class CurrentServerResponse(val server: Server?) : Message()
        data class ConnectionStatusChanged(val newStatus: ConnectionStatus) : Message()

        data class ConnectionStatusHistoryChanged(val connectionStatusHistory: List<ConnectionStatus>) :
            Message()

        data class ConnectionStatisticsChanged(
            val newStats: ConnectionStatisticsManager.ConnectionStats?
        ) : Message()

        data class VpnDisconnected(val stats: ConnectionStatistics) : Message()
        data class TimerUpdated(val newTime: Long) : Message()
        data class ErrorTimerUpdated(val newTime: Long) : Message()
        data object CurrentServerChanged : Message()
        data class AppUpdateStatusChanged(val updateStatus: UpdateStatus?) : Message()
        data class OnAppVersionResponse(val appVersion: AppVersion) : Message()

        data class OnFavouriteServerClick(val server: Server) : Message()
        data class PingUpdated(val ping: Int) : Message()

        data object OnCurrentServerClick : Message()
        data object OnConnectClicked : Message()
        data object OnDisconnectClicked : Message()
        data object OnDestroy : Message()

        data object OnResume : Message()

        data class OnDomainReachableResponse(val isReachable: Boolean) : Message()

        data class CheckReviewRequiredResponse(
            val need: Boolean,
            val stats: ConnectionStatistics
        ) : Message()
    }

    data class Dependencies(
        val deviceRepository: DeviceRepository,
        val serversRepository: ServersRepository,
        val appSettingsRepository: AppSettingsRepository,
        val userSettingsRepository: UserSettingsRepository,
        val appVersion: AppVersion,
        val updateRepository: UpdateRepository,
        val onCurrentServerClicked: () -> Unit,
        val timer: Timer,
        val connectionStatisticsManager: ConnectionStatisticsManager,
        val vpnConnectionManager: VpnConnectionManager,
        val vibratorManager: VibratorManager,
        val setCurrentServerUseCase: SetCurrentServerUseCase,
        val getSuggestedServersUseCase: GetSuggestedServersUseCase,
        val connectToVpnUseCase: ConnectToVpnUseCase,
        val connectToNextServerUseCase: ConnectToNextServerUseCase,
        val reportService: ReportService,
        val json: Json,
        val checkDeviceRegisterUseCase: CheckDeviceRegisterUseCase,
        val registerDeviceUseCase: RegisterDeviceUseCase,
        val deviceInfo: DeviceInfo,
        val pingHelper: PingHelper,
        val pingJobHandler: PingJobHandler,
        val checkReviewRequiredUseCase: CheckReviewRequiredUseCase
    )

    object Logic {

        val initialUpdate = State(
            suggestedServers = null,
            isLoading = true,
            isDomainReachable = true,
            isError = false,
            currentServer = null,
            vpnStatus = ConnectionStatus.Disconnected,
            connectionTime = 0L,
            connectionErrorTime = 0L,
            connectionStats = null,
            vpnStatusHistory = listOf(),
            updateStatus = null,
            appVersion = null
        )

        fun restore(state: State): Update<State, Message, Dependencies> = state with noEffects()

        fun update(message: Message, state: State): Update<State, Message, Dependencies> =
            when (message) {
                is Message.Init -> handleInit(state)

                is Message.SuggestedServersResponse -> handleFavoriteServersResponse(
                    message.suggestedServers,
                    state.copy(suggestedServers = message.suggestedServers)
                )

                is Message.CurrentServerResponse -> handleCurrentServerResponse(
                    message.server,
                    state
                )

                is Message.ConnectionStatusChanged -> handleConnectionStatusChanged(
                    message.newStatus,
                    state
                )

                is Message.TimerUpdated -> handleTimerUpdated(state, message.newTime)

                is Message.ConnectionStatisticsChanged -> handleConnectionStatisticsChanged(
                    state,
                    message.newStats
                )

                is Message.AppUpdateStatusChanged -> handleDownloadFileComplete(
                    state,
                    message.updateStatus
                )

                is Message.OnAppVersionResponse -> handleOnAppVersionResponse(
                    state,
                    message.appVersion
                )

                is Message.VpnDisconnected -> handleVpnDisconnected(state, message.stats)

                is Message.OnFavouriteServerClick -> handleFavouriteServerClick(
                    message.server,
                    state
                )

                is Message.OnCurrentServerClick -> handleCurrentServerClick(state)
                is Message.OnConnectClicked -> handleConnectClicked(state)
                is Message.OnDisconnectClicked -> handleDisconnectClicked(state)
                is Message.OnDestroy -> handleOnDestroy(state)

                is Message.CurrentServerChanged -> handleServerChangeComplete(state)

                is Message.ConnectionStatusHistoryChanged -> handleConnectionStatusHistoryChanged(
                    state,
                    message.connectionStatusHistory
                )

                is Message.ErrorTimerUpdated -> handleErrorTimerUpdated(state, message.newTime)

                is Message.PingUpdated -> handlePingUpdated(state, message.ping)

                is Message.OnResume -> handleOnResume(state)

                is Message.OnDomainReachableResponse -> handleOnDomainReachableResponse(
                    state,
                    message.isReachable
                )

                is Message.CheckReviewRequiredResponse -> handleCheckReviewRequireResponse(state)
            }

        private fun handleCheckReviewRequireResponse(
            state: State
        ): Update<State, Message, Dependencies> {
            return state with noEffects()
        }

        private fun handleOnDomainReachableResponse(
            state: State,
            reachable: Boolean
        ): Update<State, Message, Dependencies> {
            Logger.i("handleOnDomainReachableResponse $reachable")
            return state.copy(isDomainReachable = reachable) with noEffects()
        }

        private fun handlePingUpdated(
            state: State,
            ping: Int
        ): Update<State, Message, Dependencies> {
            return if (ping == Ping.PING_ERROR.ping) {
                state with noEffects()
            } else {
                state.copy(currentPing = ping) with noEffects()
            }
        }

        private fun handleOnResume(state: State): Update<State, Message, Dependencies> {
            return state with setOf(

            )
        }

        private fun handleOnAppVersionResponse(
            state: State,
            appVersion: AppVersion
        ): Update<State, Message, Dependencies> {
            return state.copy(appVersion = appVersion) with noEffects()
        }

        private fun handleErrorTimerUpdated(
            state: State,
            newTime: Long
        ): Update<State, Message, Dependencies> {
            return state.copy(connectionErrorTime = newTime) with noEffects()
        }

        private fun handleDownloadFileComplete(
            state: State,
            updateStatus: UpdateStatus?
        ): Update<State, Message, Dependencies> {
            return state.copy(updateStatus = updateStatus) with noEffects()
        }

        private fun handleConnectionStatusHistoryChanged(
            state: State,
            connectionStatusHistory: List<ConnectionStatus>
        ): Update<State, Message, Dependencies> {

            val connectingStatuses =
                connectionStatusHistory.filterIsInstance<ConnectionStatus.Connecting>()

            return state.copy(vpnStatusHistory = connectingStatuses.map {
                it.statusMessage ?: ""
            }) with noEffects()
        }

        private fun handleOnDestroy(state: State): Update<State, Message, Dependencies> {
            return state with setOf(
                Effects.SaveCurrentTimerValue(),
                Effects.StopNetworkStatistics()
            )
        }

        private fun handleVpnDisconnected(
            state: State,
            stats: ConnectionStatistics
        ): Update<State, Message, Dependencies> {
            return state with setOf(Effects.CheckReviewRequired(stats = stats))
        }

        private fun handleTimerUpdated(
            state: State,
            newTime: Long
        ): Update<State, Message, Dependencies> {
            return state.copy(connectionTime = newTime) with noEffects()
        }

        private fun handleConnectionStatisticsChanged(
            state: State,
            newStats: ConnectionStatisticsManager.ConnectionStats?
        ): Update<State, Message, Dependencies> {
            return state.copy(
                connectionStats = newStats,
                connectionTime = newStats?.connectionTime ?: 0L
            ) with noEffects()
        }

        private fun handleInit(
            state: State
        ): Update<State, Message, Dependencies> {
            return state with setOf(
                Effects.SubscribeToDomainStatus(),
                Effects.SubscribeToCurrentServerPing(),
                Effects.SubscribeConnectionTimer(),
                Effects.SubscribeToStatus(),
                Effects.SubscribeToStatusHistory(),
                Effects.SubscribeToNetworkStatistics(),
                Effects.SubscribeToReconnectTimer(),
                Effects.SubscribeToUpdateStatus(),
                Effects.GetAppVersion(),
                Effects.SubscribeToCurrentServer(),
                Effects.SubscribeToSuggestedServers(),
            )
        }

        private fun handleConnectionStatusChanged(
            newStatus: ConnectionStatus,
            state: State
        ): Update<State, Message, Dependencies> {
            val effects = when (newStatus) {
                is ConnectionStatus.Connected -> {
                    setOf(
                        Effects.StartNetworkStatistics(),
                        Effects.StartPinging(state.currentServer),
                        Effects.Vibrate()
                    )
                }

                is ConnectionStatus.Disconnected, is ConnectionStatus.Idle, is ConnectionStatus.Paused -> {
                    setOf(
                        Effects.StopNetworkStatistics(),
                        Effects.StopPinging()
                    )
                }

//                is ConnectionStatus.Paused -> {
//                    if (state.vpnStatus is ConnectionStatus.Connected) {
//                        setOf(Effects.StopNetworkStatistics())
//                    } else noEffects()
//                }

                is ConnectionStatus.Error -> {
                    if (state.currentServer != null) {
                        setOf(
                            Effects.SendConnectionErrorReport(
                                server = state.currentServer,
                                statusHistory = state.vpnStatusHistory
                            )
                        )
                    } else emptySet()
                }

                else -> noEffects()
            }

            return state.copy(vpnStatus = newStatus) with effects
        }

        private fun handleConnectClicked(state: State): Update<State, Message, Dependencies> {
            return state with setOfNotNull(
                Effects.ConnectVpn(state.currentServer)
            )
        }

        private fun handleDisconnectClicked(state: State): Update<State, Message, Dependencies> {
            return state with setOf(Effects.DisconnectVpn())
        }

        private fun handleCurrentServerClick(state: State): Update<State, Message, Dependencies> {
            return state with setOf(Effects.NavigateToServerList())
        }

        private fun handleFavoriteServersResponse(
            servers: SuggestedServers,
            state: State
        ): Update<State, Message, Dependencies> {
            return state.copy(
                isLoading = false,
                suggestedServers = servers
            ) with noEffects()
        }

        private fun handleFavouriteServerClick(
            server: Server,
            state: State
        ): Update<State, Message, Dependencies> {
            return state with
                    if (server.uuid == state.currentServer?.uuid &&
                        (state.vpnStatus is ConnectionStatus.Connected ||
                                state.vpnStatus is ConnectionStatus.Connecting)
                    ) noEffects()
                    else setOfNotNull(
                        Effects.SetCurrentServer(server),
                        Effects.ConnectVpn(server)
                    )
        }

        private fun handleCurrentServerResponse(
            server: Server?,
            state: State
        ): Update<State, Message, Dependencies> {
            return state.copy(
                currentServer = server
            ) with noEffects()
        }
    }

    private fun handleServerChangeComplete(state: State): Update<State, Message, Dependencies> {
        return state with noEffects()
    }

    object Effects {


        @OptIn(ExperimentalSerializationApi::class)
        class SendConnectionErrorReport(
            server: Server,
            statusHistory: List<String>
        ) : Effect<Dependencies, Message> by Effect.idle({ deps ->
            val json = Json {
                explicitNulls = true
                encodeDefaults = true
                isLenient = false
            }

            Logger.d("Config data: ${server.config}")

            deps.reportService.sendReport(
                Report.fromThrowable(
                    t = Exception("Error while connection to VPN server"),
                    metadata = mapOf(
                        "server_info" to json.encodeToJsonElement(server),
                        "connection_time_ms" to deps.timer.currentMillisFlow.value,
                        "connection_history" to statusHistory
                    )
                )
            )
        })

        class Vibrate : Effect<Dependencies, Message> by Effect.idle({ deps ->
            if (deps.userSettingsRepository.tactileFeedbackOnConnect) {
                deps.vibratorManager.vibrate()
            }
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToSuggestedServers : Effect<Dependencies, Message> by Effect.flow({ deps ->
            return@flow deps.getSuggestedServersUseCase().flatMapLatest {
                flowOf(Message.SuggestedServersResponse(it))
            }
//            return@flow deps.serversRepository.allServers.flatMapLatest {
//                flowOf(
//                    Message.SuggestedServersResponse(
//                        SuggestedServers(
//                            SuggestedServersMode.AUTO,
//                            it
//                        )
//                    )
//                )
//            }
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeConnectionTimer : Effect<Dependencies, Message> by Effect.flow({ deps ->
            return@flow deps.timer.currentMillisFlow.flatMapLatest {
                flowOf(Message.TimerUpdated(it / 1000))
            }
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToNetworkStatistics : Effect<Dependencies, Message> by Effect.flow({ deps ->
            return@flow deps.connectionStatisticsManager.stats.asStateFlow().flatMapLatest {
                flowOf(Message.ConnectionStatisticsChanged(newStats = it.wrappedValue))
            }
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToCurrentServer : Effect<Dependencies, Message> by Effect.flow({ deps ->
            return@flow deps.serversRepository.currentServer.flatMapLatest {
                flowOf(Message.CurrentServerResponse(it))
            }
        })

        class SetCurrentServer(server: Server) :
            Effect<Dependencies, Message> by Effect.single({ deps ->
                deps.setCurrentServerUseCase(server.uuid)
                return@single Message.CurrentServerChanged
            })

        class NavigateToServerList : Effect<Dependencies, Message> by Effect.onMain.idle({ deps ->
            deps.onCurrentServerClicked()
        })

        class ConnectVpn(currentServer: Server?) :
            Effect<Dependencies, Message> by Effect.onMain.idle({ deps ->
                currentServer?.let {
                    if (deps.userSettingsRepository.reconnectToNextServer) {
                        deps.connectToNextServerUseCase(it)
                    } else {
                        deps.connectToNextServerUseCase.reset()
                        deps.connectToVpnUseCase(it)
                    }
                }
            })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToStatus : Effect<Dependencies, Message> by Effect.flow({ dependencies ->
            return@flow dependencies.vpnConnectionManager.connectionStatus.flatMapLatest {
                flowOf(Message.ConnectionStatusChanged(it))
            }
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToStatusHistory :
            Effect<Dependencies, Message> by Effect.flow({ dependencies ->
                val connectionHistory = mutableListOf<ConnectionStatus>()
                return@flow dependencies.vpnConnectionManager.connectionStatus.flatMapLatest {
                    if (it is ConnectionStatus.Connecting) {
                        if (!it.statusMessage.isNullOrEmpty())
                            connectionHistory.add(it)
                    } else {
                        connectionHistory.clear()
                    }
                    flowOf(
                        Message.ConnectionStatusHistoryChanged(
                            if (connectionHistory.size <= 5) {
                                connectionHistory
                            } else {
                                connectionHistory.subList(
                                    connectionHistory.size - 6,
                                    connectionHistory.size - 1
                                )
                            }
                        )
                    )
                }
            })

        class DisconnectVpn : Effect<Dependencies, Message> by Effect.onMain.idle({ deps ->
            deps.connectToNextServerUseCase.reset()
            deps.vpnConnectionManager.disconnect()
        })

        class StartNetworkStatistics :
            Effect<Dependencies, Message> by Effect.onMain.idle({ deps ->
                val lastTimerValue = deps.appSettingsRepository.lastTimerValue

                if (lastTimerValue != null) {
                    deps.appSettingsRepository.lastTimerValue = null
                }

                deps.appSettingsRepository.lastTimerValue = 0L

                val networkStatisticsTimerType =
                    if (deps.appSettingsRepository.timeToShutdown > 0L) {
                        ConnectionStatisticsManager.ConnectionStatsTimerType.Countdown
                    } else {
                        ConnectionStatisticsManager.ConnectionStatsTimerType.Stopwatch
                    }

//                val startTime =

//                if (deps.appSettingsRepository.timeToShutdown > 0L) {
//                    deps.timer.startTimer(
//                        this,
//                        startTimerValue = deps.appSettingsRepository.timeToShutdown,
//                        timerType = Timer.TimerType.Countdown
//                    )
//                } else {
//                    deps.timer.startTimer(
//                        this,
//                        startTimerValue = lastTimerValue,
//                        timerType = Timer.TimerType.Stopwatch
//                    )
//                }

                deps.connectionStatisticsManager.startListen(
                    deps.appSettingsRepository.timeToShutdown,
                    networkStatisticsTimerType
                )
            })

        class StopNetworkStatistics :
            Effect<Dependencies, Message> by Effect.onMain.single({ deps ->
                val duration = deps.timer.currentMillisFlow.value
                val stats = deps.connectionStatisticsManager.stats.value.wrappedValue

                deps.timer.stopTimer()
                deps.connectionStatisticsManager.stopListen()

                val completeStats = ConnectionStatistics(
                    totalBytesDownloaded = stats?.totalDownloaded ?: 0,
                    totalBytesUploaded = stats?.totalUploaded ?: 0,
                    durationInMillis = duration
                )

                return@single Message.VpnDisconnected(completeStats)
            })

        class SaveCurrentTimerValue : Effect<Dependencies, Message> by Effect.onMain.idle({ deps ->
            deps.appSettingsRepository.lastTimerValue = deps.timer.currentMillisFlow.value
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToReconnectTimer : Effect<Dependencies, Message> by Effect.flow({ deps ->
            return@flow deps.connectToNextServerUseCase.currentTime.flatMapLatest {
                flowOf(Message.ErrorTimerUpdated(it))
            }
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToUpdateStatus : Effect<Dependencies, Message> by Effect.flow({ deps ->
            return@flow deps.updateRepository.status.flatMapLatest {
                flowOf(Message.AppUpdateStatusChanged(it))
            }
        })

        class GetAppVersion : Effect<Dependencies, Message> by Effect.single({ deps ->
            return@single Message.OnAppVersionResponse(deps.appVersion)
        })

        class StopPinging : Effect<Dependencies, Message> by Effect.idle({ deps ->
            deps.pingJobHandler.stopPing()
        })

        class StartPinging(server: Server?) :
            Effect<Dependencies, Message> by Effect.onDefault.idle({ deps ->
                server?.host?.let { deps.pingJobHandler.startPing(it) }
            })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToCurrentServerPing : Effect<Dependencies, Message> by Effect.flow({ deps ->
            return@flow deps.pingJobHandler.pingFlow.flatMapLatest { flowOf(Message.PingUpdated(it.ping)) }
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToDomainStatus : Effect<Dependencies, Message> by Effect.flow({ deps ->
            deps.setCurrentServerUseCase("uuid2")
            return@flow deps.appSettingsRepository.isDomainReachable.flatMapLatest {
                flowOf(Message.OnDomainReachableResponse(it))
            }
        })

        class CheckReviewRequired(val stats: ConnectionStatistics) :
            Effect<Dependencies, Message> by Effect.single({ deps ->
//                val result = deps.checkReviewRequiredUseCase()
                return@single Message.CheckReviewRequiredResponse(false, stats)
            })
    }
}
