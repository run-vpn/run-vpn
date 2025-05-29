package com.runvpn.app.feature.authorization.setpassword

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.PasswordValidationResult

interface SetPasswordComponent {

    data class State(
        val password: String,
        val confirmPassword: String,
        val isFormValid: Boolean,
        val isSavePasswordLoading: Boolean = false,
        val isGeneratePasswordLoading: Boolean = false,
        val passwordValidationResult: PasswordValidationResult?
    )

    val state: Value<State>

    fun onPasswordChanged(password: String)
    fun onConfirmPasswordChanged(value: String)

    fun onSavePasswordAndCloseClick()
    fun onGeneratePasswordAndSendToEmailClick()

    fun onCancelClick()

    sealed interface Output {
        data object PasswordSaved : Output
        data object PasswordGenerated : Output
        data object OnBack : Output
    }
}
