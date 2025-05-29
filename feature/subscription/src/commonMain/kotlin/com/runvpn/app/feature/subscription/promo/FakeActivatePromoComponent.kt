package com.runvpn.app.feature.subscription.promo

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeActivatePromoComponent(private val promo: String = "") : ActivatePromoComponent {
    override val state: Value<ActivatePromoComponent.State>
        get() = MutableValue(ActivatePromoComponent.State(promo = promo))

    override fun onPromoChanged(promo: String) {
        TODO("Not yet implemented")
    }

    override fun onActivateClick() {
        TODO("Not yet implemented")
    }
}
