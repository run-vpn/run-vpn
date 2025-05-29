package com.runvpn.app.core.analytics.analytics

data class AnalyticsEvent(
    val name: String,
    val extras: Map<String, Any> = emptyMap()
)
