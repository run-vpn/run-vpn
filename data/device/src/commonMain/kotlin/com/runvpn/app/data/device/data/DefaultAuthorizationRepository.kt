package com.runvpn.app.data.device.data

import com.runvpn.app.data.device.data.api.AuthorizationApi
import com.runvpn.app.data.device.domain.AuthorizationRepository
import com.runvpn.app.data.device.data.models.user.ContactTypeDto
import com.runvpn.app.data.device.data.models.auth.AuthRequest
import com.runvpn.app.data.device.data.models.auth.ConfirmAuthCodeRequest
import com.runvpn.app.data.device.data.models.auth.RequestAuthCodeRequest
import com.runvpn.app.data.device.data.models.auth.AuthResponse
import io.ktor.http.isSuccess

class DefaultAuthorizationRepository(
    private val authorizationApi: AuthorizationApi
) : AuthorizationRepository {


    override suspend fun login(
        type: ContactTypeDto,
        body: AuthRequest
    ): Result<AuthResponse> = runCatching {
        val response = authorizationApi.login(type.name.lowercase(), body)
        return@runCatching response.getOrThrow()
    }

    override suspend fun requestConfirmCode(
        type: ContactTypeDto,
        body: RequestAuthCodeRequest
    ): Result<Boolean> = runCatching {
        val response = authorizationApi.requestConfirmCode(type.name.lowercase(), body)
        return@runCatching response.status.isSuccess()
    }

    override suspend fun confirmCode(
        type: ContactTypeDto,
        body: ConfirmAuthCodeRequest
    ): Result<AuthResponse> = runCatching {
        val response = authorizationApi.confirmCode(type.name.lowercase(), body)
        return@runCatching response.getOrThrow()
    }

    override suspend fun generatePassword(type: ContactTypeDto): Result<Boolean> = runCatching {
        val response = authorizationApi.generatePassword(type.name.lowercase())
        return@runCatching response.isSuccess
    }

    override suspend fun logout() {
        authorizationApi.logout()
    }

}
