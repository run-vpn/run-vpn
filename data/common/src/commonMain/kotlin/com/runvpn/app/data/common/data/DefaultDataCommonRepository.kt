package com.runvpn.app.data.common.data

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.common.data.api.ReviewApi
import com.runvpn.app.data.common.domain.DataCommonRepository
import com.runvpn.app.data.common.models.CheckReviewRequireRequest
import com.runvpn.app.data.common.models.CheckReviewRequireResponse
import com.runvpn.app.data.common.models.SendReviewRequest
import io.ktor.client.statement.HttpResponse

internal class DefaultDataCommonRepository(private val reviewApi: ReviewApi) :
    DataCommonRepository {
    override suspend fun checkReviewRequired(
        checkReviewRequireRequest: CheckReviewRequireRequest
    ): ApiResponse<CheckReviewRequireResponse> =
        reviewApi.checkReviewRequired(checkReviewRequireRequest.shownAt)

    override suspend fun sendReview(
        sendReviewRequest: SendReviewRequest
    ): HttpResponse = reviewApi.sendReview(sendReviewRequest)
}
