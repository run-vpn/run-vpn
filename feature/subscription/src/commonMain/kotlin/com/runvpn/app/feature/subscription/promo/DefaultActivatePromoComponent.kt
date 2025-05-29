package com.runvpn.app.feature.subscription.promo

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage

class DefaultActivatePromoComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService
) : ActivatePromoComponent,
    ComponentContext by componentContext {

    private val _state = MutableValue(ActivatePromoComponent.State(promo = ""))
    override val state: Value<ActivatePromoComponent.State> = _state

    override fun onPromoChanged(promo: String) {
        _state.value = state.value.copy(promo = promo)
    }

    override fun onActivateClick() {
        messageService.showMessage(AppMessage.NotImplemented())
    }
}
