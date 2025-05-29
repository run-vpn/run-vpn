package com.runvpn.app.feature.subscription.activate.sharecodedialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.ClipboardManager

class DefaultShareActivationCodeComponent(
    componentContext: ComponentContext,
    private val clipboardManager: ClipboardManager,
    code: String,
    private val onDismiss: () -> Unit
) : ShareActivationCodeComponent, ComponentContext by componentContext {

    private val _state = MutableValue(
        ShareActivationCodeComponent.State(code = code)
    )
    override val state: Value<ShareActivationCodeComponent.State> = _state
    override fun onCopyLinkClick() {
        clipboardManager.copy(state.value.code)
    }

    override fun onDismissClicked() {
        onDismiss()
    }


}
