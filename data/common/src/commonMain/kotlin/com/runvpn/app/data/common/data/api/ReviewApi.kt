package com.runvpn.app.data.common.data.api

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.common.models.CheckReviewRequireResponse
import com.runvpn.app.data.common.models.SendReviewRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

interface ReviewApi {

    @GET("api/reviews/need")
    suspend fun checkReviewRequired(@Query shownAt: String?): ApiResponse<CheckReviewRequireResponse>

    @POST("api/reviews")
    suspend fun sendReview(@Body sendReviewRequest: SendReviewRequest): HttpResponse

}
