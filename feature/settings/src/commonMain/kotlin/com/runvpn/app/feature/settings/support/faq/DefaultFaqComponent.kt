package com.runvpn.app.feature.settings.support.faq

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.tea.navigation.RootRouter

class DefaultFaqComponent(
    componentContext: ComponentContext,
   private val rootRouter: RootRouter
) : FaqComponent, ComponentContext by componentContext {

    private val _state = MutableValue(
        FaqComponent.State(
            listOf()
        )
    )

    override val state: Value<FaqComponent.State>
        get() = _state

    override fun onBack() {
        rootRouter.pop()
    }

}

