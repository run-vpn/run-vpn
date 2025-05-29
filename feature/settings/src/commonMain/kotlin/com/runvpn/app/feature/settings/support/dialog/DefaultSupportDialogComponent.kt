package com.runvpn.app.feature.settings.support.dialog

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.core.common.ClipboardManager
import com.runvpn.app.core.common.UriManager
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage

internal class DefaultSupportDialogComponent(
    componentContext: ComponentContext,
    private val uriManager: UriManager,
    private val clipboardManager: ClipboardManager,
    private val messageService: MessageService,
    private val onDismissed: () -> Unit
) : SupportDialogComponent, ComponentContext by componentContext {

    override fun onCopyContactClick(contact: String) {
        clipboardManager.copy(contact)
        messageService.showMessage(AppMessage.CopyToClipboard())
    }

    override fun onOpenTelegramClick(uri: String) {
        uriManager.openUri(uri)
    }

    override fun onDismissClicked() {
        onDismissed()
    }

}
