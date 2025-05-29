package com.runvpn.app.feature.common.dialogs.shareqrcode

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class DefaultShareQrCodeComponent(
    componentContext: ComponentContext,
    link: String,
    private val onDismiss: () -> Unit
) : ShareQrCodeComponent, ComponentContext by componentContext {

    private val _state = MutableValue(
        ShareQrCodeComponent.State(
            link = link
        )
    )

    override val state: Value<ShareQrCodeComponent.State> = _state

    override fun onShareQrCodeClick() {

    }

    override fun onDismissClicked() {
        onDismiss()
    }
}
