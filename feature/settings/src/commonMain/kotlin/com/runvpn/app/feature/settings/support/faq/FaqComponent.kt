package com.runvpn.app.feature.settings.support.faq

import com.arkivanov.decompose.value.Value

interface FaqComponent {
    data class State(val questions: List<String>)

    val state: Value<State>

    fun onBack()

    sealed interface Output {
        data object OnBack : Output
    }
}

