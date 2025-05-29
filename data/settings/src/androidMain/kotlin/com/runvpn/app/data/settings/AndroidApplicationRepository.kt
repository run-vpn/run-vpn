package com.runvpn.app.data.settings

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.repositories.DeviceApplicationsRepository


class AndroidApplicationRepository(val context: Context) : DeviceApplicationsRepository {
    override suspend fun getLaunchableApps(): List<SplitTunnelingApplication>{
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val ril: List<ResolveInfo> = context.packageManager.queryIntentActivities(mainIntent, 0)

        var name: String?
        var isSystem: Boolean?
        var app: SplitTunnelingApplication?

        val apps = mutableListOf<SplitTunnelingApplication>()
        for (ri in ril) {
            if (ri.activityInfo != null) {
                val res =
                    context.packageManager.getResourcesForApplication(ri.activityInfo.applicationInfo)
                name = if (ri.activityInfo.labelRes != 0) {
                    res.getString(ri.activityInfo.labelRes)
                } else {
                    ri.activityInfo.applicationInfo.loadLabel(
                        context.packageManager
                    ).toString()
                }
                isSystem = ri.activityInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                app = SplitTunnelingApplication(ri.activityInfo.packageName, name, isSystem)
                apps.add(app)
            }
        }
        return apps.distinctBy { it.packageName }
    }
}
