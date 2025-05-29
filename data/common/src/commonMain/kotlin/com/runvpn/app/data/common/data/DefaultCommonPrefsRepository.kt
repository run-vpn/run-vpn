package com.runvpn.app.data.common.data

import com.runvpn.app.data.common.domain.CommonPrefsRepository
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class DefaultCommonPrefsRepository(private val settings: Settings) : CommonPrefsRepository {
    override var reviewLastShownAtDate: String?
        get() = settings.getStringOrNull(CommonPrefsRepository.KEY_REVIEW_LAST_SHOWN_AT_DATE)
        set(value) {
            settings[CommonPrefsRepository.KEY_REVIEW_LAST_SHOWN_AT_DATE] = value
        }
}
