package com.runvpn.app.data.common.domain.usecases

import com.runvpn.app.core.common.getDateTimeFormatted
import com.runvpn.app.data.common.domain.CommonPrefsRepository

class UpdateReviewShownDateUseCase(private val commonPrefsRepository: CommonPrefsRepository) {


    operator fun invoke() {
        commonPrefsRepository.reviewLastShownAtDate =
            getDateTimeFormatted(0, "yyyy-MM-dd'T'HH:mm:ssXXX")
    }

}
