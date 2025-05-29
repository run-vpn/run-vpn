package com.runvpn.app.data.common.domain

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.common.models.CheckReviewRequireRequest
import com.runvpn.app.data.common.models.CheckReviewRequireResponse
import com.runvpn.app.data.common.models.SendReviewRequest
import io.ktor.client.statement.HttpResponse

interface DataCommonRepository {


    suspend fun checkReviewRequired(
        checkReviewRequireRequest: CheckReviewRequireRequest
    ): ApiResponse<CheckReviewRequireResponse>

    suspend fun sendReview(sendReviewRequest: SendReviewRequest): HttpResponse

}
