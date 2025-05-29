package com.runvpn.app.feature.authorization.enterpassword

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.exceptions.ServerException
import com.runvpn.app.data.device.domain.usecases.auth.AuthByEmailUseCase
import com.runvpn.app.data.device.domain.usecases.auth.SendEmailConfirmationCodeUseCase
import com.runvpn.app.data.device.domain.usecases.auth.ValidatePasswordUseCase
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.utils.safeLaunch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class DefaultEnterPasswordComponent(
    componentContext: ComponentContext,
    exceptionHandler: CoroutineExceptionHandler,
    private val email: String,
    private val authByEmailPasswordUseCase: AuthByEmailUseCase,
    private val sendEmailConfirmationCodeUseCase: SendEmailConfirmationCodeUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val messageService: MessageService,
    private val output: (EnterPasswordComponent.Output) -> Unit
) : EnterPasswordComponent, ComponentContext by componentContext {

    companion object {
        private val logger = Logger.withTag("EnterPasswordComponent")
    }

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val _state: MutableValue<EnterPasswordComponent.State> = MutableValue(
        EnterPasswordComponent.State(
            password = "",
            passwordValidationResult = null
        )
    )
    override val state: Value<EnterPasswordComponent.State> = _state

    override fun onPasswordChanged(value: String) {
        _state.value = _state.value.copy(
            password = value,
            passwordValidationResult = validatePasswordUseCase(value)
        )
    }

    override fun onContinueClick() {
        coroutineScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            authByEmailPasswordUseCase(email, _state.value.password)
                .onFailure {
                    if (it is ServerException) {
                        _state.value = _state.value.copy(
                            isErrorCombination = true,
                            password = ""
                        )

                        messageService.showMessage(AppMessage.IncorrectAuthCombination())
                    } else {
                        _state.value = _state.value.copy(isLoading = false)
                        throw it
                    }
                }
                .onSuccess {
                    output.invoke(EnterPasswordComponent.Output.OnSuccessfulAuthorized)
                }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    override fun onAuthByEmailCodeClick() {
        _state.value = _state.value.copy(isSendCodeLoading = true)

        coroutineScope.safeLaunch(
            finallyBlock = {
                _state.value = _state.value.copy(isSendCodeLoading = false)
            }
        ) {
            sendEmailConfirmationCodeUseCase(email)
            output.invoke(EnterPasswordComponent.Output.OnAuthByCodeRequested)
        }
    }

    override fun onForgotPasswordClick() {
        messageService.showMessage(AppMessage.NotImplemented())
    }

    override fun onBackClick() {
        output.invoke(EnterPasswordComponent.Output.OnBack)
    }
}
