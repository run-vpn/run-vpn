package com.runvpn.app.feature.serverlist

import co.touchlab.kermit.Logger
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.domain.ConnectToNextServerUseCase
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.common.models.ServerSource
import com.runvpn.app.data.servers.domain.usecases.SetCurrentServerUseCase
import com.runvpn.app.data.servers.domain.usecases.SetServerFavoriteUseCase
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.runtime.coroutines.Effect
import com.runvpn.app.tea.runtime.coroutines.Update
import com.runvpn.app.tea.runtime.coroutines.noEffects
import com.runvpn.app.tea.runtime.coroutines.with
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf


object ServerListFeature {

    data class State(
        val allServers: List<Server>,
        val remoteServers: List<Pair<String?, List<Server>>>,
        val customServers: List<Server>,
        val currentServer: Server?,
        val isLoading: Boolean,
        val isError: Boolean,
        val searchQuery: String,
        val filter: ServerListComponent.ServerListFilter = ServerListComponent.ServerListFilter.ALL,
        val filteredServers: List<Pair<String?, List<Server>>>
    )

    sealed class Message {
        data object Init : Message()

        data class ServerListResponse(
            val allServers: List<Server>,
            val remoteServers: List<Pair<String?, List<Server>>>,
            val customServers: List<Server>
        ) : Message()

        data class CurrentServerResponse(val server: Server) : Message()

        data class OnServerClicked(val server: Server) : Message()
        data class OnServerFavoriteClicked(val server: Server, val favourite: Boolean) : Message()
        data class OnSearchQueryChanged(val query: String) : Message()
        data class OnFilterChanged(val filter: ServerListComponent.ServerListFilter) : Message()
    }

    data class Dependencies(
        val serversRepository: ServersRepository,
        val deviceRepository: DeviceRepository,
        val setServerFavoriteUseCase: SetServerFavoriteUseCase,
        val userSettingsRepository: UserSettingsRepository,
        val setCurrentServerUseCase: SetCurrentServerUseCase,
        val connectToVpnUseCase: ConnectToVpnUseCase,
        val connectToNextServerUseCase: ConnectToNextServerUseCase,
        val vpnConnectionManager: VpnConnectionManager,
        val messageService: MessageService,
        val onServerSelected: () -> Unit,
    )

    object Logic {
        val initialUpdate = State(
            allServers = listOf(),
            customServers = listOf(),
            remoteServers = listOf(),
            filteredServers = listOf(),
            currentServer = null,
            isLoading = true,
            isError = false,
            searchQuery = ""
        )

        fun restore(state: State): Update<State, Message, Dependencies> = state with noEffects()

        fun update(message: Message, state: State): Update<State, Message, Dependencies> =
            when (message) {
                Message.Init -> handleInit(state)
                is Message.OnServerClicked -> handleServerClick(state, message.server)
                is Message.ServerListResponse -> handleServerListResponse(
                    state,
                    message.allServers,
                    message.remoteServers,
                    message.customServers
                )

                is Message.OnServerFavoriteClicked -> handleServerFavouriteClick(
                    state,
                    message.server,
                    message.favourite
                )

                is Message.OnSearchQueryChanged -> handleSearchQueryChanged(state, message.query)
                is Message.CurrentServerResponse -> handleCurrentServerResponse(
                    message.server,
                    state
                )

                is Message.OnFilterChanged -> handleOnFilterChanged(state, message.filter)
            }

        private fun handleOnFilterChanged(
            state: State,
            filter: ServerListComponent.ServerListFilter
        ): Update<State, Message, Dependencies> {

            val filteredServers = state.allServers
                .asSequence()
                .filter { it.source != ServerSource.MINE }
                .sortedBy { it.country }
                .filter {
                    return@filter when (filter) {
                        ServerListComponent.ServerListFilter.ALL ->
                            !(it.source == ServerSource.SERVICE && !it.hasValidCountry)

                        ServerListComponent.ServerListFilter.RUN_SERVICE ->
                            (it.source == ServerSource.SERVICE && it.hasValidCountry)

                        ServerListComponent.ServerListFilter.RUN_CLIENT ->
                            it.source == ServerSource.SHARED

                        ServerListComponent.ServerListFilter.CUSTOM -> it.source == ServerSource.MINE
                    }
                }
                .groupBy { it.country }
                .toList()
                .sortedBy { it.first }
                .toList()

            val customServers = when (filter) {
                ServerListComponent.ServerListFilter.RUN_CLIENT,
                ServerListComponent.ServerListFilter.RUN_SERVICE -> listOf()

                else -> state.allServers.filter { it.source == ServerSource.MINE }
            }

            return state.copy(
                filter = filter,
                remoteServers = filteredServers,
                customServers = customServers
            ) with noEffects()
        }

        private fun handleServerFavouriteClick(
            state: State,
            server: Server,
            isFavourite: Boolean
        ): Update<State, Message, Dependencies> {
            return state with setOf(Effects.SetServerFavourite(server, isFavourite))
        }

        private fun handleServerClick(
            state: State,
            server: Server
        ): Update<State, Message, Dependencies> {
            return state with setOf(
                Effects.SetCurrentServer(server),
                Effects.ConnectToServer(server),
                Effects.NavigateToHomeScreen()
            )
        }

        private fun handleSearchQueryChanged(
            state: State,
            query: String
        ): Update<State, Message, Dependencies> {
            return state.copy(searchQuery = query) with setOf(Effects.SortListByQuery(query))
        }

        private fun handleServerListResponse(
            state: State,
            allServers: List<Server>,
            remoteServers: List<Pair<String?, List<Server>>>,
            customServers: List<Server>
        ): Update<State, Message, Dependencies> {
            Logger.d("DEBUGG!! ${allServers}")

            return state.copy(
                allServers = allServers,
                remoteServers = remoteServers,
                customServers = customServers,
                isLoading = false,
                isError = false
            ) with noEffects()
        }

        private fun handleInit(state: State): Update<State, Message, Dependencies> {
            return state.copy(isLoading = true) with setOf(
                Effects.SubscribeToServers(),
                Effects.SubscribeToCurrentServer()
            )
        }

        private fun handleCurrentServerResponse(
            server: Server,
            state: State
        ): Update<State, Message, Dependencies> {
            return state.copy(currentServer = server) with noEffects()
        }
    }

    object Effects {

        private val logger = Logger.withTag("ServerListEffects")

        class ConnectToServer(server: Server) :
            Effect<Dependencies, Message> by Effect.idle({ deps ->
                if (deps.userSettingsRepository.reconnectToNextServer) {
                    deps.connectToNextServerUseCase(server)
                } else {
                    deps.connectToNextServerUseCase.reset()
                    deps.connectToVpnUseCase(server)
                }
            })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToServers : Effect<Dependencies, Message> by Effect.flow({ deps ->
            return@flow deps.serversRepository.allServers.flatMapLatest { serversList ->
                logger.d("SubToServers collect, size is ${serversList.size}")
                val remoteServers = serversList
                    .filter {
                        !(it.source == ServerSource.SERVICE && !it.hasValidCountry) &&
                                it.source != ServerSource.MINE
                    }
                    .groupBy {
                        if (it.source == ServerSource.MINE) {
                            it.uuid
                        } else {
                            it.country
                        }
                    }
                    .toList()
                    .sortedBy { it.first }
                    .sortedByDescending { !it.second.any { it.source == ServerSource.MINE } }

                val customServers = serversList.filter { it.source == ServerSource.MINE }
                flowOf(Message.ServerListResponse(serversList, remoteServers, customServers))
            }
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class SubscribeToCurrentServer : Effect<Dependencies, Message> by Effect.flow({ deps ->
            return@flow deps.serversRepository.currentServer.filterNotNull().flatMapLatest {
                flowOf(Message.CurrentServerResponse(it))
            }
        })

        class SortListByQuery(query: String) :
            Effect<Dependencies, Message> by Effect.single({ _ ->
                return@single Message.ServerListResponse(
                    listOf(), listOf(), listOf()
                )
            })

        class NavigateToHomeScreen : Effect<Dependencies, Message> by Effect.onMain.idle({ deps ->
            deps.onServerSelected()
        })


        class SetCurrentServer(server: Server) :
            Effect<Dependencies, Message> by Effect.idle({ deps ->
                deps.setCurrentServerUseCase(server.uuid)
            })

        class SetServerFavourite(server: Server, isFavourite: Boolean) :
            Effect<Dependencies, Message> by Effect.idle({ deps ->
                deps.setServerFavoriteUseCase(server.uuid, isFavourite)
                if (isFavourite) {
                    deps.messageService.showMessage(AppMessage.AddServerToFavorites())
                }
            })
    }
}

