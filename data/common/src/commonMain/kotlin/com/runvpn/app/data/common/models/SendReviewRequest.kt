package com.runvpn.app.data.common.models

import kotlinx.serialization.Serializable

@Serializable
data class SendReviewRequest(
    /**Range 1-500*/
    val grade: Int,
    val message: String?
)
