package com.runvpn.app.feature.serveradd.xray

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.servers.domain.entities.VlessFlow
import com.runvpn.app.data.servers.domain.entities.VlessNetworkType
import com.runvpn.app.data.servers.domain.entities.VlessSecurity
import com.runvpn.app.feature.serveradd.CustomServerConfigComponent

interface XrayVpnServerConfigComponent : CustomServerConfigComponent {

    data class State(
        val host: String,
        val port: String,
        val uuid: String,
        val networkType: VlessNetworkType,
        val publicKey: String?,
        val security: VlessSecurity?,
        val flow: VlessFlow?,
        val sni: String,
        val shortId: String?,
        val networkPrimaryParam: String?,
        val networkSecondaryParam: String?,
        val isHostError: Boolean,
        val isPortError: Boolean,
        val isUuidError: Boolean
    )


    val state: Value<State>


    fun onHostChanged(host: String)
    fun onPortChanged(port: String)
    fun onUUidChanged(uuid: String)
    fun onNetworkTypeChanged(networkType: String)
    fun onPublicKeyChanged(publicKey: String)
    fun onSecurityChanged(security: String?)
    fun onFlowChanged(flow: String?)
    fun onShortIdChanged(shortId: String)
    fun onNetworkPrimaryParamChanged(networkPrimaryParam: String)
    fun onNetworkSecondaryParamChanged(networkSecondaryParam: String)

    fun onSniChanged(sni: String)
    fun onPasteClick()

    fun onQrRead(config: String)

    sealed interface Output {
        data class ConfigResult(val isConfigValid: Boolean = false) : Output
    }

}
