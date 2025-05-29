package com.runvpn.app.feature.authorization.email

import com.arkivanov.decompose.value.Value

interface EmailComponent {

    data class State(
        val email: String,
        val isEmailValid: Boolean,
        val isLoading: Boolean
    )

    val state: Value<State>

    fun onEmailChanged(email: String)

    fun onAuthByGoogleClick()
    fun onAuthByFacebookClick()
    fun onAuthByTelegramClick()

    fun onConfirmEmailClick()
    fun onCancelClick()

    sealed interface Output {
        data object OnBack : Output
        data class OnEmailConfirmed(val email: String) : Output
    }
}

