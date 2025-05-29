package com.runvpn.app.analytics

import com.runvpn.app.core.analytics.reports.Report
import com.runvpn.app.core.analytics.reports.ReportProvider
import io.sentry.kotlin.multiplatform.Sentry


class SentryReportProvider : ReportProvider {

    companion object {
        private const val KEY_CONTEXT_MESSAGE = "message"
    }

    override fun sendReport(report: Report) {
        Sentry.captureException(report.exception) { scope ->
            scope.setContext(KEY_CONTEXT_MESSAGE, report.message)
            report.metadata.forEach {  item ->
                scope.setContext(item.key, item.value ?: "null")
            }
        }
    }
}
