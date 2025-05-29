package com.runvpn.app.data.connection

actual class ExcludedAppIdsDefaults {
    actual fun getDefaultExcludedApps(): List<AppPackageId> {
        return listOf()
    }
}
