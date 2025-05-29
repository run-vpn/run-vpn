package com.runvpn.app.feature.authorization.enterpassword

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.PasswordValidationResult

interface EnterPasswordComponent {

    data class State(
        val password: String,
        val isLoading: Boolean = false,
        val isSendCodeLoading: Boolean = false,
        val isErrorCombination: Boolean = false,
        val passwordValidationResult: PasswordValidationResult?
    )

    val state: Value<State>

    fun onPasswordChanged(value: String)

    fun onContinueClick()
    fun onAuthByEmailCodeClick()
    fun onForgotPasswordClick()
    fun onBackClick()

    sealed interface Output {
        data object OnAuthByCodeRequested : Output
        data object OnSuccessfulAuthorized : Output
        data object OnBack : Output
    }
}
