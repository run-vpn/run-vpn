package com.runvpn.app.feature.serveradd.ikev2

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.servers.data.dto.CustomServerDto

class FakeIkev2ConfigComponent : Ikev2ConfigComponent {
    override val state: Value<Ikev2ConfigComponent.State>
        get() = MutableValue(
            Ikev2ConfigComponent.State(
                host = "some test host",
                username = "some test username",
                password = "some test password",
                certificateName = "VPN ROOT",
                certificate = "certificate ---begin---",
                isCertificateError = false,
                isHostError = false
            )
        )

    override fun onHostChange(host: String) {
        TODO("Not yet implemented")
    }

    override fun onUsernameChange(username: String) {
        TODO("Not yet implemented")
    }

    override fun onPasswordChange(password: String) {
        TODO("Not yet implemented")
    }

    override fun onCertificateLoaded(certificateName: String?, certificate: String?) {
        TODO("Not yet implemented")
    }

    override suspend fun getServerConfig(): CustomServerDto? {
        TODO("Not yet implemented")
    }


}
