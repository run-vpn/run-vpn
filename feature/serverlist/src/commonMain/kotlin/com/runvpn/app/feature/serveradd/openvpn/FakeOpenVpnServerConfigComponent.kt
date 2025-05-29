package com.runvpn.app.feature.serveradd.openvpn

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.servers.data.dto.CustomServerDto

class FakeOpenVpnServerConfigComponent :  OpenVpnServerConfigComponent {
    override val state: Value<OpenVpnServerConfigComponent.State>
        get() = MutableValue(
            OpenVpnServerConfigComponent.State(
                configFileName = "",
                username = "usernameTest",
                password = "passwordTest",
                ovpnUrlImport = "",
                host = "host.test",
                ovpnImportMode =  OpenVpnServerConfigComponent.OpenVpnImportMode.FILE,
                isServerAuthRequested = false,
                showHostError = null,
                showUserPassError = false,
                showUrlError = false,
                isInEditMode = false
            )
        )

    override fun onImportModeChange(importMode:  OpenVpnServerConfigComponent.OpenVpnImportMode) {
        TODO("Not yet implemented")
    }

    override fun onLoginChange(username: String) {
        TODO("Not yet implemented")
    }

    override fun onPasswordChange(password: String) {
        TODO("Not yet implemented")
    }

    override fun onImportUrlChange(url: String) {
        TODO("Not yet implemented")
    }

    override fun onReadConfigFromFile(ovpnConfig: String?) {
        TODO("Not yet implemented")
    }

    override fun onConfigFileNameLoaded(fileName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getServerConfig(): CustomServerDto? {
        TODO("Not yet implemented")
    }


}
