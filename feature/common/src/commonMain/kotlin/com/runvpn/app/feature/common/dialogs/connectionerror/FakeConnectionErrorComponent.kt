package com.runvpn.app.feature.common.dialogs.connectionerror

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeConnectionErrorComponent: ConnectionErrorComponent {
    override val state: Value<ConnectionErrorComponent.State>
        get() = MutableValue(
            ConnectionErrorComponent.State(
                urlTelegram = "https://t.me/get_latest_apkbot",
                urlApk = "https://vpn.run/latest.apk"
            )
        )

    override fun onTelegramBotClick() {
        TODO("Not yet implemented")
    }

    override fun onDownloadApkClick() {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
