package com.runvpn.app.feature.settings.support.feedback

import com.arkivanov.decompose.value.Value

interface FeedbackComponent {
    data class State(val rating: Int)

    val state: Value<State>

    fun onRatingChanged(rating: Int)
    fun onSendClick()
    fun onBack()

    sealed interface Output {
        data object OnBack : Output
    }
}
