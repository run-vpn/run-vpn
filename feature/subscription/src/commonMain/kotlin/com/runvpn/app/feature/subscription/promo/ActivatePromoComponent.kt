package com.runvpn.app.feature.subscription.promo

import com.arkivanov.decompose.value.Value

interface ActivatePromoComponent {

    data class State(
        val promo: String
    )

    val state: Value<State>

    fun onPromoChanged(promo:String)

    fun onActivateClick()

}
