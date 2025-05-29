package com.runvpn.app.data.device.domain.usecases.update

import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.data.settings.models.AppVersion

class CheckAppUpdateAvailableUseCase(
    private val updateRepository: UpdateRepository,
    private val appVersion: AppVersion,
    private val deviceInfo: DeviceInfo
) {

    suspend operator fun invoke(): Pair<Boolean, UpdateInfo> {
        val updateResponse = updateRepository.getUpdateInfo(
            packageName = deviceInfo.application.code,
            source = deviceInfo.source
        )

        val version = updateResponse.versionName

        val serverVersion = version.toVersionName()
        val currentVersion = appVersion.versionName.toVersionName()

        var compareResult = false
        if (serverVersion.first > currentVersion.first) {
            compareResult = true
        }

        if (serverVersion.first == currentVersion.first) {
            if (serverVersion.second > currentVersion.second) {
                compareResult = true
            }

            if (serverVersion.second == currentVersion.second) {
                if (serverVersion.third > currentVersion.third) {
                    compareResult = true
                }
            }
        }

        return compareResult to updateResponse
    }

    private fun String.toVersionName(): Triple<Int, Int, Int> {
        val major = this.split(".")[0].toInt()
        val minor = this.split(".")[1].toInt()
        val build = this.split(".")[2].toInt()

        return Triple(major, minor, build)
    }
}
