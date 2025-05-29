package com.runvpn.app.android.vpn.xray

import com.runvpn.app.data.common.models.VpnServer
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class MakeXrayConfigFromServerUseCase(private val appSettingsRepository: AppSettingsRepository) {

    operator fun invoke(server: VpnServer): XRayConnectConfig {
        val appUUID = appSettingsRepository.deviceUuid
        val xrayConfig = appSettingsRepository.xrayConfig
        val serverIP = server.host

        checkNotNull(appUUID)
        checkNotNull(xrayConfig)
        checkNotNull(serverIP)

        return XRayConnectConfig(
            typ = "vless",
            appUUID = appUUID,
            serverIp = serverIP,
            publicKey = xrayConfig.publicKey,
            shortId = xrayConfig.shortId,
            sni = xrayConfig.sni,
            serverPort = xrayConfig.ssPort.toString(),
            serverKey = xrayConfig.ssServerKey,
            userKey = xrayConfig.ssUserKey ?: "",
            customConfigs = server.config
        )
    }
}
