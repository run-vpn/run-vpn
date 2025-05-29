package com.runvpn.app.core.analytics.reports.impl

import co.touchlab.kermit.Logger
import com.runvpn.app.core.analytics.reports.Report
import com.runvpn.app.core.analytics.reports.ReportProvider
import com.runvpn.app.core.analytics.utils.toJsonElement
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LoggerReportProvider : ReportProvider {

    private val logger = Logger.withTag("LoggerReportProvider")

    override fun sendReport(report: Report) {
        logger.e(report.message, report.exception)

        val jsonMap = report.metadata.toJsonElement()

        val json = Json { prettyPrint = true }

        logger.e("Metadata:\n${json.encodeToString(jsonMap)}")
    }
}
