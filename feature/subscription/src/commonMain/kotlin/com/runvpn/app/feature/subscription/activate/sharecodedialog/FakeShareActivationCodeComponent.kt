package com.runvpn.app.feature.subscription.activate.sharecodedialog

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeShareActivationCodeComponent : ShareActivationCodeComponent {
    override val state: Value<ShareActivationCodeComponent.State>
        get() = MutableValue(ShareActivationCodeComponent.State(code = "fake link to apk file"))

    override fun onCopyLinkClick() {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
