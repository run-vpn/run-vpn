package com.runvpn.app.feature.subscription.activate.sharecodedialog

import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.common.dialogs.DialogComponent

interface ShareActivationCodeComponent: DialogComponent {

    data class State(
        val code: String
    )

    val state: Value<State>

    fun onCopyLinkClick()

}
