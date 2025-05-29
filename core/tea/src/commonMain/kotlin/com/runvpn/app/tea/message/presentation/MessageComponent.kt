package com.runvpn.app.tea.message.presentation

import com.arkivanov.decompose.value.Value
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.message.domain.NullWrapper

interface MessageComponent {
    val visibleMessage: Value<NullWrapper<AppMessage?>>

    fun onActionClick()
}
