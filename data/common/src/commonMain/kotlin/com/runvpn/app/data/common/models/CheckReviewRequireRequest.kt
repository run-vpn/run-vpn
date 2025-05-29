package com.runvpn.app.data.common.models

import kotlinx.serialization.Serializable

@Serializable
data class CheckReviewRequireRequest(
    val shownAt: String?
)
