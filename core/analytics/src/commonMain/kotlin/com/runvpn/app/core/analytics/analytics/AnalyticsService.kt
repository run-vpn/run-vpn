package com.runvpn.app.core.analytics.analytics

/**
 * Interface for sending analytics events.
 * Should be used in Components and Screens.
 */
interface AnalyticsService {
    fun sendEvent(event: AnalyticsEvent)
}
