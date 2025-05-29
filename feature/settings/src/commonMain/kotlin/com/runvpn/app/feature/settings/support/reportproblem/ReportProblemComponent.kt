package com.runvpn.app.feature.settings.support.reportproblem

import com.arkivanov.decompose.value.Value

interface ReportProblemComponent {

    data class State(val email: String, val isEmailValid: Boolean, val message: String)

    val state: Value<State>

    fun onEmailChange(email: String)
    fun onMessageChange(message: String)

    fun onSendClick()

    fun onBack()
    sealed interface Output {
        data object OnBack : Output
    }
}

