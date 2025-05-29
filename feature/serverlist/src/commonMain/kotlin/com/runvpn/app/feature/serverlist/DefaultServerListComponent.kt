package com.runvpn.app.feature.serverlist

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.common.models.ServerSource
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.domain.ConnectToNextServerUseCase
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.servers.domain.usecases.SetCurrentServerUseCase
import com.runvpn.app.data.servers.domain.usecases.SetServerFavoriteUseCase
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.tea.decompose.BaseComponent
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.navigation.RootRouter
import kotlinx.coroutines.CoroutineExceptionHandler

internal class DefaultServerListComponent(
    componentContext: ComponentContext,
    serversRepository: ServersRepository,
    deviceRepository: DeviceRepository,
    setServerFavoriteUseCase: SetServerFavoriteUseCase,
    userSettingsRepository: UserSettingsRepository,
    setCurrentServerUseCase: SetCurrentServerUseCase,
    connectToNextServerUseCase: ConnectToNextServerUseCase,
    messageService: MessageService,
    connectToVpnUseCase: ConnectToVpnUseCase,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val vpnConnectionManager: VpnConnectionManager,
    private val rootRouter: RootRouter,
    private val output: (ServerListComponent.Output) -> Unit
) : BaseComponent<ServerListFeature.State, ServerListFeature.Message, ServerListFeature.Dependencies>(
    initialState = ServerListFeature.Logic.initialUpdate,
    restore = ServerListFeature.Logic::restore,
    update = ServerListFeature.Logic::update,
    dependencies = ServerListFeature.Dependencies(
        serversRepository = serversRepository,
        deviceRepository = deviceRepository,
        setServerFavoriteUseCase = setServerFavoriteUseCase,
        setCurrentServerUseCase = setCurrentServerUseCase,
        connectToNextServerUseCase = connectToNextServerUseCase,
        connectToVpnUseCase = connectToVpnUseCase,
        userSettingsRepository = userSettingsRepository,
        vpnConnectionManager = vpnConnectionManager,
        messageService = messageService,
        onServerSelected = {
            output.invoke(ServerListComponent.Output.OnServerSelected)
        }
    ),
    coroutineExceptionHandler = exceptionHandler
), ServerListComponent, ComponentContext by componentContext {



    init {
        dispatch(ServerListFeature.Message.Init)
    }


    override fun onServerClicked(server: Server) {
        dispatch(ServerListFeature.Message.OnServerClicked(server))
    }

    override fun onSetServerFavouriteClicked(server: Server, isFavourite: Boolean) {
        dispatch(ServerListFeature.Message.OnServerFavoriteClicked(server, isFavourite))
    }

    override fun onSearchQueryUpdated(newQuery: String) {}

    override fun onAddVpnServerClicked() {
        output.invoke(
            if (state.value.allServers.filter { it.source == ServerSource.MINE }.isEmpty()) {
                ServerListComponent.Output.OnAddServerScreenRequest
            } else {
                ServerListComponent.Output.OnMyServersScreenRequest
            }
        )
    }

    override fun onFilterChanged(newFilter: ServerListComponent.ServerListFilter) {
        dispatch(ServerListFeature.Message.OnFilterChanged(newFilter))
    }

    override fun onPermissionGranted() {
        output(ServerListComponent.Output.OnPermissionGranted)
    }
}

