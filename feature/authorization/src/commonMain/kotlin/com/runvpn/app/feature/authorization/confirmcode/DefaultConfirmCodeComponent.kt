package com.runvpn.app.feature.authorization.confirmcode

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.usecases.auth.ConfirmEmailCodeUseCase
import com.runvpn.app.data.device.domain.usecases.auth.SendEmailConfirmationCodeUseCase
import com.runvpn.app.feature.authorization.confirmcode.ConfirmCodeComponent.Companion.REPEAT_EMAIL_REQUEST_TIME
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.utils.Timer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class DefaultConfirmCodeComponent(
    componentContext: ComponentContext,
    private val confirmEmailCodeUseCase: ConfirmEmailCodeUseCase,
    private val sendEmailConfirmationCodeUseCase: SendEmailConfirmationCodeUseCase,
    coroutineExceptionHandler: CoroutineExceptionHandler,
    private val email: String,
    private val onOutput: (ConfirmCodeComponent.Output) -> Unit
) : ConfirmCodeComponent, ComponentContext by componentContext {

    private val coroutineScope =
        createCoroutineScope(CoroutineExceptionHandler { coroutineContext, throwable ->
            _state.value = _state.value.copy(isLoading = false)
            coroutineExceptionHandler.handleException(coroutineContext, throwable)
        })
    private val timer = Timer.countdown(REPEAT_EMAIL_REQUEST_TIME)

    private val _state = MutableValue(
        ConfirmCodeComponent.State(
            confirmCode = "",
            email = email,
            isValidCode = false,
            isConfirmCodeError = false,
            isLoading = false,
            requestCodeTime = 0L
        )
    )

    override val state: Value<ConfirmCodeComponent.State> = _state

    init {
        timer.startTimer(coroutineScope)

        coroutineScope.launch {
            timer.currentMillisFlow.collectLatest {
                _state.value = state.value.copy(requestCodeTime = it / 1000L)
            }
        }
    }

    override fun onConfirmClick() {
        coroutineScope.launch {
            _state.value = state.value.copy(isLoading = true)
            val confirmationResponse = confirmEmailCodeUseCase(email, state.value.confirmCode)
            if (confirmationResponse.isSuccess) {
                if (confirmationResponse.getOrThrow().isNewUser == true) {
                    onOutput(ConfirmCodeComponent.Output.OnSetPasswordRequested)
                } else {
                    onOutput(ConfirmCodeComponent.Output.OnCodeConfirmed)
                }
            } else {
                _state.value = _state.value.copy(isValidCode = false, isConfirmCodeError = true)
            }
            _state.value = state.value.copy(isLoading = false)
        }

    }

    override fun onConfirmCodeChanged(code: String) {
        _state.value = state.value.copy(isConfirmCodeError = false)
        val tmpCode = code.filter { it.isDigit() }
        if (tmpCode.length <= ConfirmCodeComponent.CONFIRM_CODE_LENGTH)
            _state.value = state.value.copy(
                confirmCode = tmpCode,
                isValidCode = tmpCode.length == ConfirmCodeComponent.CONFIRM_CODE_LENGTH
            )
    }

    override fun onChangeEmailClick() {
        onOutput(ConfirmCodeComponent.Output.ChangeEmailRequested)
    }

    override fun onRequestNewCode() {

        coroutineScope.launch {
            val response = sendEmailConfirmationCodeUseCase(email)
            if (response.isSuccess) {
                timer.startTimer(coroutineScope)
            }
        }
    }

    override fun onCancelClick() {
        onOutput.invoke(ConfirmCodeComponent.Output.OnBack)
    }

}

