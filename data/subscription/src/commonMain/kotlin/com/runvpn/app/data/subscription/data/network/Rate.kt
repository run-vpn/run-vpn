package com.runvpn.app.data.subscription.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatesResponse(
    @SerialName("items")
    val items: List<Rate>
)

@Serializable
data class Rate(
    @SerialName("uuid")
    val id: String,
    @SerialName("isActive")
    val isActive: Boolean,
    @SerialName("deviceCountMin")
    val deviceCountMin: Int,
    @SerialName("deviceCountMax")
    val deviceCountMax: Int,
    @SerialName("deviceStartCoefficient")
    val deviceStartCoefficient: Float,
    @SerialName("deviceStepCoefficient")
    val deviceStepCoefficient: Float,
    @SerialName("period")
    val periodInDays: Int,
    @SerialName("priority")
    val priority: Int,
    @SerialName("promotion")
    val promotion: String? = null
)
