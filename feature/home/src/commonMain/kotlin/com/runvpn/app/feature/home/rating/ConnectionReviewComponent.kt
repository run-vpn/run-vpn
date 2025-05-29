package com.runvpn.app.feature.home.rating

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.domain.ConnectionStatistics
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.feature.common.dialogs.DialogComponent

interface ConnectionReviewComponent : DialogComponent {

    data class State(
        val connectionStats: ConnectionStatistics,
        val server: Server,
        val rating: Int,
        val isBadRating: Boolean,
        val isTextFieldForReasonVisible: Boolean,
        val feedbackMessage: String,
    )

    val state: Value<State>

    fun onRatingChanged(rating: Int)
    fun onFeedbackMessageChange(message:String)
    fun onBadReasonClicked(reason: String)
    fun onSendReviewClicked()
}
