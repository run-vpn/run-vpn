package com.runvpn.app.tea.message.data

import com.runvpn.app.tea.message.domain.AppMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class DefaultMessageService : MessageService {

    private val messageChannel = Channel<AppMessage>(Channel.UNLIMITED)

    override val messageFlow = messageChannel.receiveAsFlow()

    override fun showMessage(message: AppMessage) {
        messageChannel.trySend(message)
    }
}
