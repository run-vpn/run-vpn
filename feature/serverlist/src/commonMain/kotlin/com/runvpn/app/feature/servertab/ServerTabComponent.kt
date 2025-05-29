package com.runvpn.app.feature.servertab

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.serverlist.ServerListComponent

interface ServerTabComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class ServerList(val serverListComponent: ServerListComponent) : Child
    }

    sealed interface Output {
        data object OnServerSelected : Output
        data object OnMyServersScreenRequested: Output
        data object OnAddServerScreenRequested: Output
        data object OnPermissionGranted : Output
    }
}

