package com.runvpn.app.data.common.domain.usecases

import com.runvpn.app.data.common.domain.CommonPrefsRepository
import com.runvpn.app.data.common.domain.DataCommonRepository
import com.runvpn.app.data.common.models.CheckReviewRequireRequest

class CheckReviewRequiredUseCase(
    private val dataCommonRepository: DataCommonRepository,
    private val commonPrefsRepository: CommonPrefsRepository
) {


    suspend operator fun invoke() =
        runCatching {
            val request = CheckReviewRequireRequest(
                shownAt = commonPrefsRepository.reviewLastShownAtDate
            )
            val response = dataCommonRepository.checkReviewRequired(request)
            return@runCatching response.getOrThrow().need
        }

}
