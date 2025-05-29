package com.runvpn.app.data.connection.domain

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionStatistics(
    val totalBytesDownloaded: Long,
    val totalBytesUploaded: Long,
    val durationInMillis: Long,
)
