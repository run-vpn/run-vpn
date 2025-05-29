package com.runvpn.app.feature.settings.dns.adddialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.domain.usecases.CheckIPv4ValidUseCase
import com.runvpn.app.core.common.domain.usecases.CheckIPv6ValidUseCase
import com.runvpn.app.data.connection.domain.AddDnsServerUseCase
import com.runvpn.app.data.connection.domain.CreateDnsServerResult
import com.runvpn.app.data.connection.domain.DnsServer
import com.runvpn.app.data.connection.domain.DnsServersRepository
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.utils.safeLaunch
import kotlinx.coroutines.CoroutineExceptionHandler

class DefaultCreateDnsServerDialogComponent(
    componentContext: ComponentContext,
    private val dnsServersRepository: DnsServersRepository,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val checkIPv4ValidUseCase: CheckIPv4ValidUseCase,
    private val checkIPv6ValidUseCase: CheckIPv6ValidUseCase,
    private val addDnsServerUseCase: AddDnsServerUseCase,
    private val onCompleted: (DnsServer) -> Unit,
    private val onDismiss: () -> Unit
) : CreateDnsServerDialogComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val _state: MutableValue<CreateDnsServerDialogComponent.State> = MutableValue(
        CreateDnsServerDialogComponent.State(
            name = "",
            ip = ""
        )
    )
    override val state: Value<CreateDnsServerDialogComponent.State> = _state

    override fun onDismissClicked() {
        onDismiss()
    }

    override fun onAddServerClick() {
        _state.value = state.value.copy(isLoading = true)
        coroutineScope.safeLaunch(finallyBlock = {
            _state.value = state.value.copy(isLoading = false)
        }) {
            val server = DnsServer.createEmpty(
                name = _state.value.name,
                ip = _state.value.ip
            )
            when (val result = addDnsServerUseCase(server)) {
                CreateDnsServerResult.ErrorAlreadyCreated -> {
                    _state.value = _state.value.copy(isAlreadyCreatedError = true)
                }

                CreateDnsServerResult.ErrorEmptyIp -> {
                    _state.value = _state.value.copy(isIpEmptyError = true)
                }

                CreateDnsServerResult.ErrorEmptyName -> {
                    _state.value = _state.value.copy(isNameError = true)

                }

                CreateDnsServerResult.ErrorIpInvalid -> {
                    _state.value = _state.value.copy(isIpFormatError = true)
                }

                is CreateDnsServerResult.Success -> {
                    onCompleted(result.createdServer)
                }
            }
        }
    }

    override fun onServerNameChanged(newValue: String) {
        _state.value = _state.value.copy(name = newValue)
    }

    override fun onServerAddressChanged(newValue: String) {
        _state.value = _state.value.copy(ip = newValue)

        if (checkIpValid(newValue)) {
            _state.value = _state.value.copy(isIpFormatError = false)
        }
    }

    private fun checkIpValid(ip: String): Boolean {
        return checkIPv4ValidUseCase(ip) || checkIPv6ValidUseCase(ip)
    }
}
