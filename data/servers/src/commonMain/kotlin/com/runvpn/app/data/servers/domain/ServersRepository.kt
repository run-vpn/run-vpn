package com.runvpn.app.data.servers.domain

import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.common.models.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


interface ServersRepository {

    val currentServer: StateFlow<Server?>
    val allServers: StateFlow<List<Server>>

    suspend fun initServers()
    suspend fun syncServers()

    suspend fun getById(uuid: String): Server?

    fun setCurrentServer(uuid: String?)

    suspend fun addCustomServer(server: CustomServerDto)
    suspend fun editCustomServer(uuid: String, server: CustomServerDto)
    suspend fun deleteCustomServer(uuid: String)

    suspend fun setServerFavorite(uuid: String, isFavourite: Boolean)
    suspend fun setLastConnectionTime(uuid: String, timeInMillis: Long)

    @Deprecated("Can use __allServers.value__")
    fun getAllServersSync(): List<Server>

    @Deprecated("Servers sync. Reactive logic is back!!")
    suspend fun getAllServers(): Flow<List<Server>>

    fun getLatestConnectedXrayServer(): Server?

    fun clearServers()
}
