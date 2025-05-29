package com.runvpn.app.data.device.domain.usecases.auth

import com.runvpn.app.data.device.data.models.user.ContactTypeDto
import com.runvpn.app.data.device.domain.AuthorizationRepository
import com.runvpn.app.data.device.data.models.auth.ConfirmAuthCodeRequest
import com.runvpn.app.data.device.data.models.auth.AuthResponse
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class ConfirmEmailCodeUseCase(
    private val authorizationRepository: AuthorizationRepository,
    private val appSettingsRepository: AppSettingsRepository
) {

    suspend operator fun invoke(email: String, emailToken: String): Result<AuthResponse> =
        runCatching {
            val response = authorizationRepository.confirmCode(
                ContactTypeDto.EMAIL,
                ConfirmAuthCodeRequest(
                    value = email,
                    code = emailToken
                )
            )

            if (response.isSuccess) {
                appSettingsRepository.email = email
                appSettingsRepository.appToken = response.getOrThrow().token
            }

            return response
        }

}
