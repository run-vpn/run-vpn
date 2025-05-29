package com.runvpn.app.feature.common.dialogs.connectionerror

import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.common.dialogs.DialogComponent

interface ConnectionErrorComponent: DialogComponent {

    data class State(
        val urlTelegram: String,
        val urlApk: String
    )

    val state: Value<State>

    fun onTelegramBotClick()

    fun onDownloadApkClick()

}
