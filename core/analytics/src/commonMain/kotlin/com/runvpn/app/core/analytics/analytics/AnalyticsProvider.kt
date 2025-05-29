package com.runvpn.app.core.analytics.analytics

/**
 * Describes analytics provider.
 *
 * A list of it will be provided to AnalyticsService via Koin DI module.
 * This interface should be used to implement providers like Firebase, AppMetrica, etc.
 * Don't use it directly in Components or Screens.
 */
interface AnalyticsProvider {

    /**
     * Sends an [AnalyticsEvent] to analytics providers.
     */
    fun sendEvent(event: AnalyticsEvent)
}
