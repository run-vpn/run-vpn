package com.runvpn.app.data.device.domain

import com.runvpn.app.data.device.data.models.user.ContactTypeDto
import com.runvpn.app.data.device.data.models.auth.AuthRequest
import com.runvpn.app.data.device.data.models.auth.ConfirmAuthCodeRequest
import com.runvpn.app.data.device.data.models.auth.RequestAuthCodeRequest
import com.runvpn.app.data.device.data.models.auth.AuthResponse

interface AuthorizationRepository {

    suspend fun login(type: ContactTypeDto, body: AuthRequest): Result<AuthResponse>
    suspend fun requestConfirmCode(type: ContactTypeDto, body: RequestAuthCodeRequest): Result<Boolean>
    suspend fun confirmCode(type: ContactTypeDto, body: ConfirmAuthCodeRequest): Result<AuthResponse>
    suspend fun generatePassword(type: ContactTypeDto): Result<Boolean>
    suspend fun logout()
}
