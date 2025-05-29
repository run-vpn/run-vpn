package com.runvpn.app.data.settings.domain.usecases


import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.repositories.DeviceApplicationsRepository

class GetSortedApplicationsListUseCase(
    private val applicationsRepository: DeviceApplicationsRepository,
    private val appPackage: String
) {

    suspend operator fun invoke(): List<SplitTunnelingApplication> {
        return applicationsRepository.getLaunchableApps()
            .filter { it.packageName != appPackage }
            .sortedWith(compareBy({ it.isSystem }, { it.name }))
    }
}
