package com.runvpn.app.feature.authorization.setpassword

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.usecases.auth.SendNewPasswordToEmailUseCase
import com.runvpn.app.data.device.domain.usecases.auth.SetUserPasswordUseCase
import com.runvpn.app.data.device.domain.usecases.auth.ValidatePasswordUseCase
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.utils.safeLaunch
import kotlinx.coroutines.CoroutineExceptionHandler

internal class DefaultSetPasswordComponent(
    componentContext: ComponentContext,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val setUserPasswordUseCase: SetUserPasswordUseCase,
    private val sendNewPasswordToEmailUseCase: SendNewPasswordToEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val onOutput: (SetPasswordComponent.Output) -> Unit
) : SetPasswordComponent, ComponentContext by componentContext {

    companion object {
        private const val MINIMUM_PASSWORD_LENGTH = 6
    }

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val _state = MutableValue(
        SetPasswordComponent.State(
            password = "",
            confirmPassword = "",
            isFormValid = false,
            passwordValidationResult = null
        )
    )
    override val state: Value<SetPasswordComponent.State> = _state

    override fun onPasswordChanged(password: String) {
        _state.value = state.value.copy(password = password)
        updateFormValid()
    }

    override fun onConfirmPasswordChanged(value: String) {
        _state.value = _state.value.copy(confirmPassword = value)
        updateFormValid()
    }

    override fun onSavePasswordAndCloseClick() {
        coroutineScope.safeLaunch(
            finallyBlock = {
                _state.value = _state.value.copy(isSavePasswordLoading = false)
            }
        ) {
            _state.value = _state.value.copy(isSavePasswordLoading = true)
            setUserPasswordUseCase(_state.value.password, _state.value.confirmPassword)
                .onSuccess { onOutput(SetPasswordComponent.Output.PasswordSaved) }
                .onFailure { throw it }
        }
    }

    override fun onGeneratePasswordAndSendToEmailClick() {
        coroutineScope.safeLaunch(
            finallyBlock = {
                _state.value = _state.value.copy(isGeneratePasswordLoading = false)
            }
        ) {
            _state.value = _state.value.copy(isGeneratePasswordLoading = true)
            sendNewPasswordToEmailUseCase()
                .onSuccess { onOutput(SetPasswordComponent.Output.PasswordGenerated) }
                .onFailure { throw it }
        }
    }

    override fun onCancelClick() {
        onOutput(SetPasswordComponent.Output.OnBack)
    }

    private fun updateFormValid() {
        val result = (_state.value.password == _state.value.confirmPassword) &&
                _state.value.password.length >= MINIMUM_PASSWORD_LENGTH

        _state.value = _state.value.copy(
            passwordValidationResult = validatePasswordUseCase(
                _state.value.password,
                _state.value.confirmPassword
            )
        )
    }
}

