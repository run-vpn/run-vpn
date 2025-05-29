package com.runvpn.app.data.device.domain.usecases.auth

import com.runvpn.app.data.device.data.models.user.ContactTypeDto
import com.runvpn.app.data.device.domain.AuthorizationRepository

class SendNewPasswordToEmailUseCase(
    private val authorizationRepository: AuthorizationRepository
) {


    suspend operator fun invoke(): Result<Boolean> {

        return authorizationRepository.generatePassword(ContactTypeDto.EMAIL)
    }
}
