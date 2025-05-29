package com.runvpn.app.core.analytics.analytics.impl

import com.runvpn.app.core.analytics.analytics.AnalyticsEvent
import com.runvpn.app.core.analytics.analytics.AnalyticsProvider
import com.runvpn.app.core.analytics.analytics.AnalyticsService

/**
 * Sends event to all available providers.
 */
class DefaultAnalyticsService(
    private val providers: List<AnalyticsProvider>,
    private val defaultExtras: Map<String, Any>? = null
) : AnalyticsService {

    override fun sendEvent(event: AnalyticsEvent) {
        val resultEvent = if (defaultExtras == null) {
            event
        } else {
            event.copy(extras = event.extras + defaultExtras)
        }

        providers.forEach { it.sendEvent(resultEvent) }
    }
}
