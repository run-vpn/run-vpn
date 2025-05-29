package com.runvpn.app.feature.home.welcome

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.AppUseMode

interface WelcomeComponent {

    data class State(
        val selectedUseMode: AppUseMode,
        val hasCloseButton:Boolean
    )

    val state: Value<State>

    fun onUsageModeClick(useMode: AppUseMode)
    fun onConfirmClick()

    sealed interface Output {

        data class OnConfirm(val hasBackStack: Boolean) : Output
    }

}
