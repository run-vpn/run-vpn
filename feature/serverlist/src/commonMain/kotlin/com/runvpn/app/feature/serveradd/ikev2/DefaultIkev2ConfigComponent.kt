package com.runvpn.app.feature.serveradd.ikev2

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.usecases.CreateCustomServerUseCase
import com.runvpn.app.data.servers.utils.CustomConfigFields

class DefaultIkev2ConfigComponent(
    componentContext: ComponentContext,
    private val createCustomServerUseCase: CreateCustomServerUseCase,
    private val serverToEdit: Server?,
    private val output: (Ikev2ConfigComponent.Output) -> Unit
) : Ikev2ConfigComponent, ComponentContext by componentContext {

    companion object {
        const val IKEV2_CERTIFICATE_FILE_MIME_TYPE = "application/x-pem-file"
    }

    private val _state = MutableValue(
        Ikev2ConfigComponent.State(
            host = serverToEdit?.config?.get(CustomConfigFields.IKEV2_FIELD_HOST) ?: "",
            username = serverToEdit?.config?.get(CustomConfigFields.IKEV2_FIELD_USERNAME) ?: "",
            password = serverToEdit?.config?.get(CustomConfigFields.IKEV2_FIELD_PASSWORD) ?: "",
            certificateName = serverToEdit?.config?.get(CustomConfigFields.IKEV2_FIELD_CERTIFICATE_NAME)
                ?: "",
            certificate = serverToEdit?.config?.get(CustomConfigFields.IKEV2_FIELD_CERTIFICATE)
                ?: "",
            isCertificateError = false,
            isHostError = false
        )
    )

    override val state: Value<Ikev2ConfigComponent.State>
        get() = _state

    override fun onHostChange(host: String) {
        _state.value = state.value.copy(host = host, isHostError = false)
        checkFormValidation()
    }

    override fun onUsernameChange(username: String) {
        _state.value = state.value.copy(username = username)
        checkFormValidation()
    }

    override fun onPasswordChange(password: String) {
        _state.value = state.value.copy(password = password)
        checkFormValidation()
    }

    override fun onCertificateLoaded(certificateName: String?, certificate: String?) {
        if (certificateName.isNullOrEmpty() || certificate.isNullOrEmpty()) {
            _state.value = state.value.copy(isCertificateError = true)
            return
        }
        _state.value =
            state.value.copy(certificateName = certificateName, certificate = certificate)
        checkFormValidation()
    }

    override suspend fun getServerConfig(): CustomServerDto? {
        if (state.value.host.isEmpty()) {
            _state.value = state.value.copy(isHostError = true)
            return null
        }

        if (state.value.certificate.isEmpty()) {
            _state.value = state.value.copy(isCertificateError = true)
            return null
        }

        val config = mapOf(
            Pair(CustomConfigFields.IKEV2_FIELD_HOST, state.value.host),
            Pair(CustomConfigFields.IKEV2_FIELD_USERNAME, state.value.username),
            Pair(CustomConfigFields.IKEV2_FIELD_PASSWORD, state.value.password),
            Pair(CustomConfigFields.IKEV2_FIELD_CERTIFICATE_NAME, state.value.certificateName),
            Pair(CustomConfigFields.IKEV2_FIELD_CERTIFICATE, state.value.certificate),
        )

        return createCustomServerUseCase(
            host = state.value.host,
            config = config,
            protocol = ConnectionProtocol.IKEV2
        )
    }

    private fun checkFormValidation() {
        output(
            Ikev2ConfigComponent.Output.ConfigResult(
                state.value.host.isNotEmpty() &&
                        state.value.username.isNotEmpty() &&
                        state.value.password.isNotEmpty() &&
                        state.value.certificateName.isNotEmpty() &&
                        state.value.certificate.isNotEmpty()
            )
        )
    }

}
