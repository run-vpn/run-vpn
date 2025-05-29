package com.runvpn.app.feature.servertab

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.serverlist.ServerListComponent
import com.runvpn.app.feature.serverlist.createServerListComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import kotlinx.serialization.Serializable

internal class DefaultServerTabComponent(
    componentContext: ComponentContext,
    private val decomposeComponentFactory: DecomposeComponentFactory,
    private val onOutput: (ServerTabComponent.Output) -> Unit
) : ServerTabComponent, ComponentContext by componentContext {


    companion object {
        private const val SERVER_TAB_CHILD_STACK_KEY = "server_tab_child_stack_key"
    }

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: Value<ChildStack<*, ServerTabComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.ServerList,
        key = SERVER_TAB_CHILD_STACK_KEY,
        childFactory = ::createChild,
        handleBackButton = true
    )

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): ServerTabComponent.Child =
        when (config) {

            is ChildConfig.ServerList -> ServerTabComponent.Child.ServerList(
                decomposeComponentFactory.createServerListComponent(
                    componentContext,
                    ::handleServerListOutput
                )
            )

        }

    private fun handleServerListOutput(output: ServerListComponent.Output) {
        when (output) {
            is ServerListComponent.Output.OnServerSelected -> {
                onOutput.invoke(ServerTabComponent.Output.OnServerSelected)
            }

            is ServerListComponent.Output.OnMyServersScreenRequest -> {
                onOutput.invoke(ServerTabComponent.Output.OnMyServersScreenRequested)
            }

            is ServerListComponent.Output.OnAddServerScreenRequest -> {
                onOutput.invoke(ServerTabComponent.Output.OnAddServerScreenRequested)
            }

            ServerListComponent.Output.OnPermissionGranted -> {
                onOutput.invoke(ServerTabComponent.Output.OnPermissionGranted)
            }
        }
    }


    @Serializable
    private sealed interface ChildConfig {

        @Serializable
        data object ServerList : ChildConfig

    }

}

