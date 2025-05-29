package com.runvpn.app.data.device.domain.usecases.auth

import com.runvpn.app.data.device.domain.models.PasswordValidationResult

class ValidatePasswordUseCase {

    companion object {
        private const val MINIMUM_PASSWORD_LENGTH = 6
    }

    operator fun invoke(password: String, confirmPassword: String? = null): PasswordValidationResult {
        return if (password.isEmpty()) {
            PasswordValidationResult.ErrorEmpty
        } else if (password.length < MINIMUM_PASSWORD_LENGTH) {
            PasswordValidationResult.ErrorMinimumSymbols
        } else if (confirmPassword != null && password != confirmPassword) {
            PasswordValidationResult.ErrorConfirmPasswordDoesNotMatch
        } else PasswordValidationResult.Valid
    }
}
