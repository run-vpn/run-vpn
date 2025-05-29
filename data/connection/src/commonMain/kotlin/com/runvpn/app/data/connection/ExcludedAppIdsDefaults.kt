package com.runvpn.app.data.connection

expect class ExcludedAppIdsDefaults {
    fun getDefaultExcludedApps(): List<AppPackageId>
}
