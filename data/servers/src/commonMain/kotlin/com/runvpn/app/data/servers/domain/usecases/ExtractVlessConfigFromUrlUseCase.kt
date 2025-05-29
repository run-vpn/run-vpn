package com.runvpn.app.data.servers.domain.usecases

import co.touchlab.kermit.Logger
import com.runvpn.app.data.servers.domain.entities.ConfigValidationResult
import com.runvpn.app.data.servers.domain.entities.ImportedXrayConfig
import com.runvpn.app.data.servers.domain.entities.VlessFlow
import com.runvpn.app.data.servers.domain.entities.VlessNetworkType
import com.runvpn.app.data.servers.domain.entities.VlessSecurity
import com.runvpn.app.data.servers.utils.CustomConfigFields
import io.ktor.http.Url

class ExtractVlessConfigFromUrlUseCase {

    operator fun invoke(url: String): ConfigValidationResult {
        // IN DEVELOPMENT!
        try {
            val parsingUrl = Url(url)
            val subProtocol = parsingUrl.protocol.name
            val uuid = parsingUrl.user
            val host = parsingUrl.host
            val serverPort = parsingUrl.port.toString()
            val pbk = parsingUrl.parameters[CustomConfigFields.XRAY_FIELD_PBK]
            val sid = parsingUrl.parameters[CustomConfigFields.XRAY_FIELD_SID]
            val sni = parsingUrl.parameters[CustomConfigFields.XRAY_FIELD_SNI]
            val type = parsingUrl.parameters[CustomConfigFields.XRAY_FIELD_NETWORK_TYPE]
            val security = parsingUrl.parameters[CustomConfigFields.XRAY_FIELD_SECURITY]
            val flow = parsingUrl.parameters[CustomConfigFields.XRAY_FIELD_FLOW]
            val encryption = parsingUrl.parameters[CustomConfigFields.XRAY_FIELD_ENCRYPTION]
            val networkPrimaryParam =
                parsingUrl.parameters[CustomConfigFields.XRAY_FIELD_NETWORK_PRIMARY_PARAM]
            val networkSecondaryParam =
                parsingUrl.parameters[CustomConfigFields.XRAY_FIELD_NETWORK_SECONDARY_PARAM]
            val profileName = url.split("#").last()

            Logger.i("Parsing Config! $parsingUrl")

            Logger.i(
                "Parsing Config! " +
                        "subProtocol = $subProtocol, uuid = $uuid, serverHost = $host, " +
                        "servePort = $serverPort, pbk = $pbk, sid = $sid, type = $type, " +
                        "profileName = $profileName"
            )

            checkNotNull(uuid)
            checkNotNull(type)

            val result = ImportedXrayConfig(
                subProtocol = subProtocol,
                user = uuid,
                host = host,
                port = serverPort,
                encryption = encryption,
                flow = VlessFlow.entries.find { it.value == flow },
                profileName = profileName,
                paramNetworkType = VlessNetworkType.valueOf(type.uppercase()),
                security = VlessSecurity.entries.find { it.name == security?.uppercase() },
                paramPublicKey = pbk,
                paramShortId = sid,
                sni = sni,
                networkPrimaryParam = networkPrimaryParam,
                networkSecondaryParam = networkSecondaryParam
            )

            return ConfigValidationResult.Success(host, result)

        } catch (e: Exception) {
            return ConfigValidationResult.ConfigInvalid
        }

    }

}
