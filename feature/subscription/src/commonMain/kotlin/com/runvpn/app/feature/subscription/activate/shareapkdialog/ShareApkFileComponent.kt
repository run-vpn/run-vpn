package com.runvpn.app.feature.subscription.activate.shareapkdialog

import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.common.dialogs.DialogComponent

interface ShareApkFileComponent : DialogComponent {

    data class State(
        val link: String
    )

    val state: Value<State>

    fun onCopyLinkClick()
}
