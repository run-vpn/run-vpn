package com.runvpn.app.data.device.domain.models

sealed interface PasswordValidationResult {
    data object Valid : PasswordValidationResult

    data object ErrorEmpty : PasswordValidationResult
    data object ErrorMinimumSymbols : PasswordValidationResult
    data object ErrorConfirmPasswordDoesNotMatch : PasswordValidationResult
}

val PasswordValidationResult.isValid: Boolean
    get() = this is PasswordValidationResult.Valid
