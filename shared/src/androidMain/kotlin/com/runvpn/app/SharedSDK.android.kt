package com.runvpn.app

import co.touchlab.kermit.Logger
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.settings.models.AppVersion
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryOptions


actual fun initializeSentry(
    deviceInfo: DeviceInfo,
    appVersion: AppVersion,
    environment: String,
    isDebug: Boolean
) {
    val versionName = appVersion.versionName
    val buildNumber = appVersion.versionCode
    val source = deviceInfo.source

    Logger.d("Sentry init: $versionName, $buildNumber, $source")

    val configuration: (SentryOptions) -> Unit = {
        it.beforeSend = { event -> if (!isDebug) event else null }

        it.dsn = "https://b4af8e5f89245f5262c8d41789fa9a3e@xsentry.net/69"
        it.release = "runvpn@$versionName+$buildNumber"
        it.environment = environment
    }

    Sentry.configureScope {
        it.setTag("Source", source)
    }

    Sentry.init(configuration)
}
