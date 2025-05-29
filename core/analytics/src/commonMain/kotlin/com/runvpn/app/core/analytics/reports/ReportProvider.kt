package com.runvpn.app.core.analytics.reports

/**
 * Describes report provider.
 *
 * A list of it will be provided to ReportService via Koin DI module.
 * This interface should be used to implement providers like Firebase, Sentry, etc.
 * Don't use it directly in Components or Screens.
 */
interface ReportProvider {
    fun sendReport(report: Report)
}
