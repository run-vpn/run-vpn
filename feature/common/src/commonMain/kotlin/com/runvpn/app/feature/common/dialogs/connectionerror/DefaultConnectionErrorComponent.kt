package com.runvpn.app.feature.common.dialogs.connectionerror

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.UriManager
import com.runvpn.app.core.common.UrlConstants

class DefaultConnectionErrorComponent(
    componentContext: ComponentContext,
    private val uriManager: UriManager,
    private val onDismiss: () -> Unit
) : ConnectionErrorComponent, ComponentContext by componentContext {

    private val _state = MutableValue(
        ConnectionErrorComponent.State(
            urlTelegram = UrlConstants.TELEGRAM_APK_BOT,
            urlApk = UrlConstants.WEBSITE_APK
        )
    )

    override val state: Value<ConnectionErrorComponent.State> = _state


    override fun onTelegramBotClick() {
        uriManager.openUri(state.value.urlTelegram)
    }

    override fun onDownloadApkClick() {
        uriManager.openUri(state.value.urlApk)
    }

    override fun onDismissClicked() {
        onDismiss()
    }
}
