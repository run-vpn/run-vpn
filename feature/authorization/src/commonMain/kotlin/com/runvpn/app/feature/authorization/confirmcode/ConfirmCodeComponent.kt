package com.runvpn.app.feature.authorization.confirmcode

import com.arkivanov.decompose.value.Value

interface ConfirmCodeComponent {

    companion object {
        const val CONFIRM_CODE_LENGTH = 5
        const val REPEAT_EMAIL_REQUEST_TIME = 60 * 1000L
    }

    data class State(
        val confirmCode: String,
        val email: String,
        val isValidCode: Boolean,
        val isConfirmCodeError: Boolean,
        val isLoading: Boolean,
        val requestCodeTime: Long
    )

    val state: Value<State>

    fun onConfirmCodeChanged(code: String)

    fun onChangeEmailClick()
    fun onRequestNewCode()
    fun onConfirmClick()

    fun onCancelClick()

    sealed interface Output {
        data object OnCodeConfirmed : Output
        data object OnSetPasswordRequested : Output
        data object ChangeEmailRequested : Output

        data object OnBack : Output
    }
}

