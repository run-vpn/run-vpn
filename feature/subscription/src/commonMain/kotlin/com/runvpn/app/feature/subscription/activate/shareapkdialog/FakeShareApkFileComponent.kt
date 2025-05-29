package com.runvpn.app.feature.subscription.activate.shareapkdialog

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeShareApkFileComponent : ShareApkFileComponent {
    override val state: Value<ShareApkFileComponent.State>
        get() = MutableValue(ShareApkFileComponent.State(link = "fake link to apk file"))

    override fun onCopyLinkClick() {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
