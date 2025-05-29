package com.runvpn.app.data.device.data.api

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.device.data.models.auth.AuthRequest
import com.runvpn.app.data.device.data.models.auth.ConfirmAuthCodeRequest
import com.runvpn.app.data.device.data.models.auth.RequestAuthCodeRequest
import com.runvpn.app.data.device.data.models.auth.AuthResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

interface AuthorizationApi {

    @POST("api/auth/{type}/login")
    suspend fun login(
        @Path("type") type: String,
        @Body body: AuthRequest
    ): ApiResponse<AuthResponse>

    @POST("api/auth/{type}/code")
    suspend fun requestConfirmCode(
        @Path("type") type: String,
        @Body body: RequestAuthCodeRequest
    ): HttpResponse

    @POST("api/auth/{type}/")
    suspend fun confirmCode(
        @Path("type") type: String,
        @Body body: ConfirmAuthCodeRequest
    ): ApiResponse<AuthResponse>

    @POST("api/auth/{type}/password")
    suspend fun generatePassword(
        @Path("type") type: String
    ): ApiResponse<String>

    @DELETE("api/auth")
    suspend fun logout(): HttpResponse

}
