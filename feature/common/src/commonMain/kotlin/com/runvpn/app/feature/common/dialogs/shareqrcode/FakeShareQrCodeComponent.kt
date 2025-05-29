package com.runvpn.app.feature.common.dialogs.shareqrcode

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeShareQrCodeComponent : ShareQrCodeComponent {
    override val state: Value<ShareQrCodeComponent.State>
        get() = MutableValue(ShareQrCodeComponent.State(link = "testLinkToShare"))

    override fun onShareQrCodeClick() {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
