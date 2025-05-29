package com.runvpn.app.feature.serveradd.ikev2

import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.serveradd.CustomServerConfigComponent

interface Ikev2ConfigComponent : CustomServerConfigComponent {

    data class State(
        val host: String,
        val username: String,
        val password: String,
        val certificateName: String,
        val certificate: String,
        val isCertificateError: Boolean,
        val isHostError: Boolean,
    )

    val state: Value<State>


    fun onHostChange(host: String)

    fun onUsernameChange(username: String)

    fun onPasswordChange(password: String)

    fun onCertificateLoaded(certificateName: String?, certificate: String?)

    sealed interface Output {
        data class ConfigResult(val isConfigValid: Boolean = false) : Output
    }

}
