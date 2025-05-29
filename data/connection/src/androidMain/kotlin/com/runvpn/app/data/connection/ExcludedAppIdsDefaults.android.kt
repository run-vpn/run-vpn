package com.runvpn.app.data.connection

import android.content.Context

actual class ExcludedAppIdsDefaults(private val context: Context) {
    actual fun getDefaultExcludedApps(): List<AppPackageId> {
        return listOf(
            AppPackageId("com.android.vending"),
            // Application for check functionality
            // AppPackageId("cz.webprovider.whatismyipaddress"),
        )
    }
}
