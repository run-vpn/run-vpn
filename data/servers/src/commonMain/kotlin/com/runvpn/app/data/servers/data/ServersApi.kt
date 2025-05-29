package com.runvpn.app.data.servers.data

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.servers.data.dto.SendCustomConfigResponse
import com.runvpn.app.data.servers.data.dto.ServersResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path

interface ServersApi {

    @GET("api/servers")
    suspend fun getAllServers(): ApiResponse<ServersResponse>

    @POST("api/servers/")
    suspend fun sendCustomServer(
        @Body customServerDto: CustomServerDto
    ): ApiResponse<SendCustomConfigResponse>


    @PUT("api/servers/{id}")
    suspend fun editCustomServer(
        @Path("id") uuid: String,
        @Body customServerDto: CustomServerDto
    )

    @DELETE("api/servers/{id}")
    suspend fun deleteCustomServer(
        @Path("id") uuid: String
    )

    @POST("api/servers/{id}/favorite")
    suspend fun addServerToFavorite(
        @Path("id") uuid: String
    )

    @DELETE("api/servers/{id}/favorite")
    suspend fun deleteServerFromFavorite(
        @Path("id") uuid: String
    )
}
