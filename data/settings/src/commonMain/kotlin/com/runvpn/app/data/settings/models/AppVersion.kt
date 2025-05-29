package com.runvpn.app.data.settings.models

data class AppVersion(
    val versionCode: Long,
    val versionName: String,
    val platform: String = "Android"
)
