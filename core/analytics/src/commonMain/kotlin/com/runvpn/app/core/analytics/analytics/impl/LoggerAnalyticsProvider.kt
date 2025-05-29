package com.runvpn.app.core.analytics.analytics.impl

import co.touchlab.kermit.Logger
import com.runvpn.app.core.analytics.analytics.AnalyticsEvent
import com.runvpn.app.core.analytics.analytics.AnalyticsProvider
import com.runvpn.app.core.analytics.utils.toJsonElement
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LoggerAnalyticsProvider : AnalyticsProvider {

    private val logger = Logger.withTag("LoggerAnalyticsProvider")

    override fun sendEvent(event: AnalyticsEvent) {
        val jsonMap = event.extras.toJsonElement()
        val json = Json { prettyPrint = true }

        logger.i("Event: ${event.name}, extras:\n${json.encodeToString(jsonMap)}")
    }
}
