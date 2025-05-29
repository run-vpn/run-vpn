package com.runvpn.app.core.analytics.reports

/**
 * Interface for sending app error reports.
 * Should be used in Components and Screens.
 */
interface ReportService {
    fun sendReport(report: Report)
}
