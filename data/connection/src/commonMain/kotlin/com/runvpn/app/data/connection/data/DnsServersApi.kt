package com.runvpn.app.data.connection.data

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.connection.data.dto.DeleteDnsServerResponse
import com.runvpn.app.data.connection.data.dto.GetDnsServersResponse
import com.runvpn.app.data.connection.data.dto.PostDnsServerRequest
import com.runvpn.app.data.connection.data.dto.PostDnsServerResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path

interface DnsServersApi {

    @GET("api/dns-servers")
    suspend fun getDnsServers(): ApiResponse<GetDnsServersResponse>

    @POST("api/dns-servers")
    suspend fun postDnsServer(
        @Body dnsServer: PostDnsServerRequest
    ): ApiResponse<PostDnsServerResponse>

    @PUT("api/dns-servers/{dnsServerId}")
    suspend fun updateDnsServer(
        @Path dnsServerId: Long,
        @Body dnsServer: PostDnsServerRequest
    ): ApiResponse<PostDnsServerResponse>

    @DELETE("api/dns-servers/{dnsServerId}")
    suspend fun deleteDnsServer(
        @Path dnsServerId: Long
    ): ApiResponse<DeleteDnsServerResponse>

}
