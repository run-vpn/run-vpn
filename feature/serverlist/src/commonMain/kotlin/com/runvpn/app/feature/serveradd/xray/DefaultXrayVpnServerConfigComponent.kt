package com.runvpn.app.feature.serveradd.xray

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.ClipboardManager
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.servers.domain.entities.ConfigValidationResult
import com.runvpn.app.data.servers.domain.entities.ImportedXrayConfig
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.entities.VlessFlow
import com.runvpn.app.data.servers.domain.entities.VlessNetworkType
import com.runvpn.app.data.servers.domain.entities.VlessSecurity
import com.runvpn.app.data.servers.domain.usecases.CreateCustomServerUseCase
import com.runvpn.app.data.servers.domain.usecases.ExtractVlessConfigFromUrlUseCase
import com.runvpn.app.data.servers.utils.CustomConfigFields
import com.runvpn.app.data.servers.utils.TmpXrayConfigJson.createXrayConfig

class DefaultXrayVpnServerConfigComponent(
    componentContext: ComponentContext,
    private val clipboardManager: ClipboardManager,
    private val extractVlessConfigFromUrlUseCase: ExtractVlessConfigFromUrlUseCase,
    private val createCustomServerUseCase: CreateCustomServerUseCase,
    private val serverToEdit: Server? = null,
    private val output: (XrayVpnServerConfigComponent.Output) -> Unit
) : XrayVpnServerConfigComponent, ComponentContext by componentContext {

    private val _state = MutableValue(
        XrayVpnServerConfigComponent.State(
            host = serverToEdit?.host ?: "",
            port = serverToEdit?.config?.get(CustomConfigFields.XRAY_FIELD_PORT) ?: "",
            uuid = serverToEdit?.config?.get(CustomConfigFields.XRAY_FIELD_UUID) ?: "",
            networkType = VlessNetworkType.entries.toTypedArray()
                .find { it.name == serverToEdit?.config?.get(CustomConfigFields.XRAY_FIELD_NETWORK_TYPE) }
                ?: VlessNetworkType.TCP,
            publicKey = serverToEdit?.config?.get(CustomConfigFields.XRAY_FIELD_PBK),
            shortId = serverToEdit?.config?.get(CustomConfigFields.XRAY_FIELD_SID),
            security = VlessSecurity.entries.toTypedArray()
                .find { it.name == serverToEdit?.config?.get(CustomConfigFields.XRAY_FIELD_SECURITY) },
            flow = VlessFlow.entries.toTypedArray().find {
                it.value == serverToEdit?.config?.get(CustomConfigFields.XRAY_FIELD_FLOW)
            },
            networkPrimaryParam =
            serverToEdit?.config?.get(CustomConfigFields.XRAY_FIELD_NETWORK_PRIMARY_PARAM),
            sni = "",
            networkSecondaryParam =
            serverToEdit?.config?.get(CustomConfigFields.XRAY_FIELD_NETWORK_SECONDARY_PARAM),
            isHostError = false,
            isPortError = false,
            isUuidError = false
        )
    )

    override val state: Value<XrayVpnServerConfigComponent.State>
        get() = _state


    override fun onHostChanged(host: String) {
        _state.value = state.value.copy(host = host, isHostError = false)
        checkFormValidation()
    }

    override fun onPortChanged(port: String) {
        _state.value = state.value.copy(port = port, isPortError = false)
        checkFormValidation()
    }

    override fun onUUidChanged(uuid: String) {
        _state.value = state.value.copy(uuid = uuid)
        checkFormValidation()
    }

    override fun onNetworkTypeChanged(networkType: String) {
        _state.value = state.value.copy(networkType = VlessNetworkType.valueOf(networkType))
    }

    override fun onPublicKeyChanged(publicKey: String) {
        _state.value = state.value.copy(publicKey = publicKey)
    }

    override fun onSecurityChanged(security: String?) {
        _state.value =
            state.value.copy(security = VlessSecurity.entries.find { it.name == security })
    }

    override fun onFlowChanged(flow: String?) {
        _state.value =
            state.value.copy(flow = VlessFlow.entries.find { it.name == flow })
    }

    override fun onShortIdChanged(shortId: String) {
        _state.value = state.value.copy(shortId = shortId)
    }

    override fun onNetworkPrimaryParamChanged(networkPrimaryParam: String) {
        _state.value = state.value.copy(networkPrimaryParam = networkPrimaryParam)
    }

    override fun onNetworkSecondaryParamChanged(networkSecondaryParam: String) {
        _state.value = state.value.copy(networkSecondaryParam = networkSecondaryParam)
    }

    override fun onSniChanged(sni: String) {
        _state.value = state.value.copy(sni = sni)
    }

    override fun onPasteClick() {
        clipboardManager.paste()?.let {
            importingConfig(it)
        }
    }

    private fun importingConfig(config: String) {
        val result = extractVlessConfigFromUrlUseCase(config)

        when (result) {
            is ConfigValidationResult.Success<*> -> {
                val cfg = result.config as ImportedXrayConfig
                _state.value = state.value.copy(
                    host = cfg.host,
                    port = cfg.port,
                    uuid = cfg.user,
                    sni = cfg.sni ?: "",
                    publicKey = cfg.paramPublicKey,
                    shortId = cfg.paramShortId,
                    networkType = cfg.paramNetworkType,
                    flow = cfg.flow,
                    security = cfg.security,
                    networkPrimaryParam = cfg.networkPrimaryParam,
                    networkSecondaryParam = cfg.networkSecondaryParam
                )
                output.invoke(XrayVpnServerConfigComponent.Output.ConfigResult(isConfigValid = true))
            }

            is ConfigValidationResult.AuthRequired -> {
                //ignored
            }

            is ConfigValidationResult.ConfigInvalid -> {
                //ignored for now
            }
        }
    }

    override fun onQrRead(config: String) {
        importingConfig(config)
    }


    override suspend fun getServerConfig(): CustomServerDto? {
        if (state.value.host.isEmpty()) {
            _state.value = state.value.copy(isHostError = true)
            return null
        }

        val port = state.value.port.toIntOrNull()
        if (port == null) {
            _state.value = state.value.copy(isPortError = true)
            return null
        }

        if (state.value.uuid.isEmpty()) {
            _state.value = state.value.copy(isUuidError = true)
            return null
        }


        val xrayConfig = createXrayConfig(
            uuid = state.value.uuid,
            ip = state.value.host,
            port = port,
            networkType = state.value.networkType.name,
            security = state.value.security?.name ?: "none",
            flow = state.value.flow?.value ?: "",
            shortId = state.value.shortId ?: "",
            publicKey = state.value.publicKey ?: "",
            networkPrimaryParam = state.value.networkPrimaryParam ?: "",
            networkSecondaryParam = state.value.networkSecondaryParam ?: "",
            sni = state.value.sni,
        )

        val config = mapOf(
            Pair(CustomConfigFields.XRAY_FIELD_HOST, state.value.host),
            Pair(CustomConfigFields.XRAY_FIELD_PORT, state.value.port),
            Pair(CustomConfigFields.XRAY_FIELD_UUID, state.value.uuid),
            Pair(CustomConfigFields.XRAY_FIELD_NETWORK_TYPE, state.value.networkType.name),
            Pair(CustomConfigFields.XRAY_FIELD_PBK, state.value.publicKey),
            Pair(CustomConfigFields.XRAY_FIELD_SID, state.value.shortId),
            Pair(CustomConfigFields.XRAY_FIELD_SECURITY, state.value.security?.name),
            Pair(CustomConfigFields.XRAY_FIELD_FLOW, state.value.flow?.name),
            Pair(
                CustomConfigFields.XRAY_FIELD_NETWORK_PRIMARY_PARAM,
                state.value.networkPrimaryParam
            ),
            Pair(
                CustomConfigFields.XRAY_FIELD_NETWORK_SECONDARY_PARAM,
                state.value.networkSecondaryParam
            ),
            Pair(CustomConfigFields.XRAY_FIELD_CONFIG, xrayConfig),
        )

        return createCustomServerUseCase(
            host = state.value.host,
            protocol = ConnectionProtocol.XRAY,
            config = config
        )
    }

    private fun checkFormValidation() {
        if (
            state.value.host.isNotEmpty() &&
            state.value.port.isNotEmpty() &&
            state.value.uuid.isNotEmpty()
        ) {
            output.invoke(XrayVpnServerConfigComponent.Output.ConfigResult(isConfigValid = true))
        }
    }
}
