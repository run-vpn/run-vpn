package com.runvpn.app.feature.home.trafficover

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.AppUseMode
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface ChooseUsageModeComponent : SimpleDialogComponent {

    data class State(
        val selectedUseMode: AppUseMode
    )

    val state: Value<State>

    fun onUsageModeClick(useMode: AppUseMode)
    fun onConfirmClick()
    fun onReadFullTermsClick()

    sealed interface Output {

        interface OnConfirm : Output
    }

}
