package com.runvpn.app.tea.message.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.message.domain.NullWrapper

class FakeMessageComponent : MessageComponent {

    override val visibleMessage: Value<NullWrapper<AppMessage?>> =
        MutableValue(NullWrapper(
            AppMessage.Common("Some long mmmmmmmmmmmessage here", actionTitle = "Action!")
        ))

    override fun onActionClick() {
        TODO("Not yet implemented")
    }
}
