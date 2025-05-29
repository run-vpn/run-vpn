package com.runvpn.app.data.device.domain.usecases.auth

import com.runvpn.app.data.device.data.models.user.ContactTypeDto
import com.runvpn.app.data.device.domain.AuthorizationRepository
import com.runvpn.app.data.device.data.models.auth.RequestAuthCodeRequest

class SendEmailConfirmationCodeUseCase(
    private val authorizationRepository: AuthorizationRepository
) {


    suspend operator fun invoke(email: String): Result<Boolean> {
        val result = runCatching {
            authorizationRepository.requestConfirmCode(
                type = ContactTypeDto.EMAIL,
                body = RequestAuthCodeRequest(
                    value = email
                )
            )
        }.map { it.isSuccess }

        return result
    }
}
