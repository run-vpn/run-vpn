package com.runvpn.app.data.settings

import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.repositories.DeviceApplicationsRepository

class IosApplicationRepository : DeviceApplicationsRepository {

    override suspend fun getLaunchableApps(): List<SplitTunnelingApplication> {
        return listOf()
    }
}
