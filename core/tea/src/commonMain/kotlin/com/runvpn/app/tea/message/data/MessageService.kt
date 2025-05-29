package com.runvpn.app.tea.message.data

import com.runvpn.app.tea.message.domain.AppMessage
import kotlinx.coroutines.flow.Flow

interface MessageService {

    val messageFlow: Flow<AppMessage>

    fun showMessage(message: AppMessage)
}
