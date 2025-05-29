package com.runvpn.app.feature.common.dialogs.shareqrcode

import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.common.dialogs.DialogComponent

interface ShareQrCodeComponent : DialogComponent {

    data class State(
        val link: String
    )

    val state: Value<State>

    fun onShareQrCodeClick()

}
