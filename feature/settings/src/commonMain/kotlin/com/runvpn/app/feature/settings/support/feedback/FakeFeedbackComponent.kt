package com.runvpn.app.feature.settings.support.feedback

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeFeedbackComponent : FeedbackComponent {
    override val state: Value<FeedbackComponent.State>
        get() = MutableValue(FeedbackComponent.State(rating = 3))

    override fun onRatingChanged(rating: Int) {
        TODO("Not yet implemented")
    }

    override fun onSendClick() {
        TODO("Not yet implemented")
    }

    override fun onBack() {
        TODO("Not yet implemented")
    }

}

