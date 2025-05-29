package com.runvpn.app.feature.home.rating

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnResume
import com.runvpn.app.data.common.domain.usecases.SendReviewUseCase
import com.runvpn.app.data.common.domain.usecases.UpdateReviewShownDateUseCase
import com.runvpn.app.data.connection.domain.ConnectionStatistics
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.tea.decompose.createCoroutineScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

internal class DefaultConnectionReviewComponent(
    componentContext: ComponentContext,
    private val sendReviewUseCase: SendReviewUseCase,
    private val updateReviewShownDateUseCase: UpdateReviewShownDateUseCase,
    coroutineExceptionHandler: CoroutineExceptionHandler,
    private val connectionStats: ConnectionStatistics,
    private val server: Server,
    private val onDismissed: () -> Unit
) : ConnectionReviewComponent, ComponentContext by componentContext {

    //getDateTimeFormatted(0, "yyyy-MM-dd'T'HH:mm:ssXXX")
    companion object {
        private const val BAD_RATING = 3
    }

    private val _state: MutableValue<ConnectionReviewComponent.State> = MutableValue(
        ConnectionReviewComponent.State(
            connectionStats = connectionStats,
            server = server,
            isBadRating = false,
            rating = 0,
            isTextFieldForReasonVisible = false,
            feedbackMessage = ""
        )
    )
    override val state: Value<ConnectionReviewComponent.State> = _state

    private val coroutineScope = createCoroutineScope(coroutineExceptionHandler)

    init {
        lifecycle.doOnResume {
            updateReviewShownDateUseCase()
        }
    }

    override fun onRatingChanged(rating: Int) {
        _state.value = _state.value.copy(rating = rating, isBadRating = checkRatingIsBad(rating))
    }

    override fun onFeedbackMessageChange(message: String) {
        _state.value = state.value.copy(feedbackMessage = message)
    }

    override fun onBadReasonClicked(reason: String) {
        TODO("Show reason field")
    }

    override fun onSendReviewClicked() {
        coroutineScope.launch {
            val grade = state.value.rating * 100
            val message = state.value.feedbackMessage
            sendReviewUseCase(grade = grade, message = message)
            onDismissed()
        }
    }

    override fun onDismissClicked() {
        onDismissed()
    }

    private fun checkRatingIsBad(rating: Int): Boolean = rating <= BAD_RATING
}
