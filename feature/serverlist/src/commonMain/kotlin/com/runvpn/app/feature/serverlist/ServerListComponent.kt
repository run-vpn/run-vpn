package com.runvpn.app.feature.serverlist

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.Server

interface ServerListComponent {

    enum class ServerListFilter {
        ALL,
        RUN_SERVICE,
        RUN_CLIENT,
        CUSTOM
    }

    val state: Value<ServerListFeature.State>

    fun onServerClicked(server: Server)
    fun onSetServerFavouriteClicked(server: Server, isFavourite: Boolean)
    fun onSearchQueryUpdated(newQuery: String)
    fun onAddVpnServerClicked()
    fun onFilterChanged(newFilter: ServerListFilter)

    fun onPermissionGranted()

    sealed interface Output {
        data object OnServerSelected : Output
        data object OnMyServersScreenRequest : Output
        data object OnAddServerScreenRequest : Output
        data object OnPermissionGranted : Output
    }
}
