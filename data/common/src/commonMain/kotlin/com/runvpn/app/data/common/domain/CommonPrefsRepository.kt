package com.runvpn.app.data.common.domain

interface CommonPrefsRepository {

    companion object{
        internal const val KEY_REVIEW_LAST_SHOWN_AT_DATE = "key_review_last_shown_at_date"
    }


    var reviewLastShownAtDate: String?
}
