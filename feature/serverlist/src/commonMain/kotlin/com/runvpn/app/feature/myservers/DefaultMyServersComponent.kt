package com.runvpn.app.feature.myservers

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.ServerSource
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.navigation.RootChildConfig
import com.runvpn.app.tea.navigation.RootRouter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

internal class DefaultMyServersComponent(
    componentContext: ComponentContext,
    exceptionHandler: CoroutineExceptionHandler,
    private val serversRepository: ServersRepository,
    private val rootRouter: RootRouter,
    private val output: (MyServersComponent.Output) -> Unit
) : MyServersComponent, ComponentContext by componentContext {


    private val _state = MutableValue(MyServersComponent.State(servers = listOf()))
    override val state: Value<MyServersComponent.State>
        get() = _state

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    init {
        coroutineScope.launch {
            serversRepository.allServers.collect { servers ->
                _state.value = _state.value.copy(
                    servers = servers.filter { it.source == ServerSource.MINE }
                )
            }
        }
    }

    override fun onAddServerClick() {
        output.invoke(MyServersComponent.Output.OnAddServerScreenRequested(null))
    }

    override fun onBackClick() {
        output.invoke(MyServersComponent.Output.OnBack)
    }

    override fun onEditServerClick(uuid: String) {
        rootRouter.open(RootChildConfig.AddServer(uuid))
    }

    override fun onDeleteServerClick(uuid: String) {
        coroutineScope.launch {
            serversRepository.deleteCustomServer(uuid)
        }
    }

}
