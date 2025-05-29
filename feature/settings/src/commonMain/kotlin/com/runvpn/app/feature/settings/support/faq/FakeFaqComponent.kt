package com.runvpn.app.feature.settings.support.faq

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeFaqComponent : FaqComponent {
    override val state: Value<FaqComponent.State>
        get() = MutableValue(FaqComponent.State(questions = listOf("first", "second")))

    override fun onBack() {
        TODO("Not yet implemented")
    }

}

