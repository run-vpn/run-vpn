package com.runvpn.app.feature.settings.support.feedback

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.tea.navigation.RootRouter

class DefaultFeedbackComponent(
    componentContext: ComponentContext,
    private val rootRouter: RootRouter
) : FeedbackComponent, ComponentContext by componentContext {

    private val _state = MutableValue(FeedbackComponent.State(rating = 0))
    override val state: Value<FeedbackComponent.State>
        get() = _state

    override fun onRatingChanged(rating: Int) {
        _state.value = state.value.copy(rating = rating)
    }

    override fun onSendClick() {
        // Send feedback
        rootRouter.pop()
    }

    override fun onBack() {
        rootRouter.pop()
    }

}
