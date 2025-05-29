package com.runvpn.app.feature.home.rating

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.domain.ConnectionStatistics
import com.runvpn.app.data.servers.utils.TestData

class FakeConnectionReviewComponent : ConnectionReviewComponent {
    override val state: Value<ConnectionReviewComponent.State> = MutableValue(
        ConnectionReviewComponent.State(
            connectionStats = ConnectionStatistics(200_000, 180_000, 48_000),
            server = TestData.testServer4,
            rating = 4,
            isBadRating = false,
            isTextFieldForReasonVisible = false,
            feedbackMessage = ""
        )
    )

    override fun onSendReviewClicked() {
        TODO("Not yet implemented")
    }

    override fun onRatingChanged(rating: Int) {
        TODO("Not yet implemented")
    }

    override fun onFeedbackMessageChange(message: String) {
        TODO("Not yet implemented")
    }

    override fun onBadReasonClicked(reason: String) {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
