package com.runvpn.app.feature.subscription.activate.shareapkdialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.ClipboardManager

class DefaultShareApkFileComponent(
    componentContext: ComponentContext,
    private val clipboardManager: ClipboardManager,
    link: String,
    private val onDismiss: () -> Unit
) : ShareApkFileComponent, ComponentContext by componentContext {

    private val _state = MutableValue(
        ShareApkFileComponent.State(link = link)
    )
    override val state: Value<ShareApkFileComponent.State> = _state
    override fun onCopyLinkClick() {
        clipboardManager.copy(state.value.link)
    }

    override fun onDismissClicked() {
        onDismiss()
    }


}
