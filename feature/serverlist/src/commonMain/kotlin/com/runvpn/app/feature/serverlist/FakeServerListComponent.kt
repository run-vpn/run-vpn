package com.runvpn.app.feature.serverlist

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.utils.TestData

class FakeServerListComponent(private val isLoading: Boolean = false) : ServerListComponent {
    override val state: Value<ServerListFeature.State>
        get() = MutableValue(
            ServerListFeature.State(
                TestData.testServerList,
                remoteServers = listOf(
//                    "Germany" to TestData.testServerList.map { it.copy(iso = "ru") },
//                    "USA" to TestData.testServerList,
//                    "Russia" to TestData.testServerList,
                ),
                filteredServers = listOf(),
                customServers = TestData.customServerList,
                isLoading = isLoading,
                isError = false,
                searchQuery = "Some search",
                currentServer = TestData.testServer3
            )
        )


    override fun onServerClicked(server: Server) {
        TODO("Not yet implemented")
    }

    override fun onSetServerFavouriteClicked(server: Server, isFavourite: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSearchQueryUpdated(newQuery: String) {
        TODO("Not yet implemented")
    }

    override fun onAddVpnServerClicked() {
        TODO("Not yet implemented")
    }

    override fun onFilterChanged(newFilter: ServerListComponent.ServerListFilter) {
        TODO("Not yet implemented")
    }

    override fun onPermissionGranted() {
        TODO("Not yet implemented")
    }


}

