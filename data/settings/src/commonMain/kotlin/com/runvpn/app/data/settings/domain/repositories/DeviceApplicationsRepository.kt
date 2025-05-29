package com.runvpn.app.data.settings.domain.repositories

import com.runvpn.app.data.settings.domain.SplitTunnelingApplication

interface DeviceApplicationsRepository {
    suspend fun getLaunchableApps():List<SplitTunnelingApplication>
}
