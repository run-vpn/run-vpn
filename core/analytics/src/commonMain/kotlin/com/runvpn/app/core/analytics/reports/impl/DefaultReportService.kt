package com.runvpn.app.core.analytics.reports.impl

import com.runvpn.app.core.analytics.reports.Report
import com.runvpn.app.core.analytics.reports.ReportProvider
import com.runvpn.app.core.analytics.reports.ReportService

/**
 * Sends reports to all available providers.
 */
class DefaultReportService(
    private val providers: List<ReportProvider>,
    private val defaultMetadata: Map<String, Any>? = null
) : ReportService {

    override fun sendReport(report: Report) {
        val resultReport = if (defaultMetadata == null) {
            report
        } else {
            report.copy(metadata = report.metadata + defaultMetadata)
        }

        providers.forEach { it.sendReport(resultReport) }
    }
}
