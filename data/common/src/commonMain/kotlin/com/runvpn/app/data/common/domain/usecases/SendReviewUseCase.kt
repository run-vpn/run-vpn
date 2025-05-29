package com.runvpn.app.data.common.domain.usecases

import com.runvpn.app.data.common.domain.DataCommonRepository
import com.runvpn.app.data.common.models.SendReviewRequest
import io.ktor.http.isSuccess

class SendReviewUseCase(private val dataCommonRepository: DataCommonRepository) {


    suspend operator fun invoke(grade: Int, message: String?) = runCatching {
        val sendReviewRequest = SendReviewRequest(grade = grade, message)
        val response = dataCommonRepository.sendReview(sendReviewRequest)
        return@runCatching response.status.isSuccess()
    }

}
