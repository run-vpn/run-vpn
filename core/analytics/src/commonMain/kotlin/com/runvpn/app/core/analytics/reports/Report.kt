package com.runvpn.app.core.analytics.reports

data class Report(
    val message: String,
    val exception: Throwable,
    val metadata: Map<String, Any?> = emptyMap()
) {
    companion object {

        fun fromThrowable(t: Throwable, metadata: Map<String, Any?> = emptyMap()): Report {
            return Report(
                message = t.message ?: "empty",
                exception = t,
                metadata = metadata
            )
        }
    }
}
