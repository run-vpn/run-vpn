package com.runvpn.app.feature.serveradd.oversocks

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.servers.data.dto.CustomServerDto

class FakeOverSocksConfigComponent : OverSocksConfigComponent {
    override val state: Value<OverSocksConfigComponent.State>
        get() = MutableValue(
            OverSocksConfigComponent.State(
                host = "127.0.0.1",
                port = "80",
                username = "username",
                password = "password",
                udpOverTcp = true
            )
        )

    override fun onHostChange(host: String) {
        TODO("Not yet implemented")
    }

    override fun onPortChange(port: String) {
        TODO("Not yet implemented")
    }

    override fun onUserNameChange(username: String) {
        TODO("Not yet implemented")
    }

    override fun onPasswordChange(password: String) {
        TODO("Not yet implemented")
    }

    override fun onUdpOverTcpToggled(isEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onPasteClick() {
        TODO("Not yet implemented")
    }

    override fun onLoadConfig(config: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getServerConfig(): CustomServerDto? {
        TODO("Not yet implemented")
    }


}
