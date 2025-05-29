package com.runvpn.app.feature.settings.dns

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.doOnResume
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.data.defaultDnsServers
import com.runvpn.app.data.connection.domain.DeleteDnsServerUseCase
import com.runvpn.app.data.connection.domain.DnsServer
import com.runvpn.app.data.connection.domain.DnsServersRepository
import com.runvpn.app.data.connection.domain.SetDnsServerUseCase
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.feature.settings.dns.adddialog.createCreateDnsServerDialog
import com.runvpn.app.tea.createCommonDialog
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import com.runvpn.app.tea.dialog.DialogMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

internal class DefaultChooseDnsServerComponent(
    private val componentContext: ComponentContext,
    private val componentFactory: DecomposeComponentFactory,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val vpnConnectionManager: VpnConnectionManager,
    private val dnsServersRepository: DnsServersRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val setDnsServerUseCase: SetDnsServerUseCase,
    private val deleteDnsServerUseCase: DeleteDnsServerUseCase
) : ChooseDnsServerComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val _state: MutableValue<ChooseDnsServerComponent.State> = MutableValue(
        ChooseDnsServerComponent.State(
            defaultServers = defaultDnsServers,
            servers = listOf(),
            selectedServer = null,
        )
    )
    override val state: Value<ChooseDnsServerComponent.State> = _state

    private val dialogNavigation: SlotNavigation<SlotConfig> = SlotNavigation()
    override val childSlot: Value<ChildSlot<*, SimpleDialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = SlotConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createDialogChild
    )

    private val backCallback = BackCallback {
        showReconnectDialog()
    }

    private var lastChosenDnsServerIp = appSettingsRepository.selectedDnsServerIP

    init {
        backHandler.register(backCallback)
        backCallback.isEnabled = false
        lifecycle.doOnResume {
            refreshServers()
        }
    }

    private fun createDialogChild(
        slotConfig: SlotConfig,
        componentContext: ComponentContext
    ): SimpleDialogComponent {
        return when (slotConfig) {
            SlotConfig.CreateDnsDialog -> componentFactory.createCreateDnsServerDialog(
                componentContext = componentContext,
                onDismiss = { dialogNavigation.dismiss() },
                onCompleted = {
                    refreshServers()
                    dialogNavigation.dismiss()
                }
            )

            is SlotConfig.AlertDialog -> componentFactory.createCommonDialog(
                componentContext,
                dialogMessage = slotConfig.dialogMessage,
                onConfirm = slotConfig.onConfirm,
                onDismiss = slotConfig.onDismiss
            )
        }
    }

    override fun onAddServerClick() {
        dialogNavigation.activate(SlotConfig.CreateDnsDialog)
    }


    override fun onDnsServerClick(server: DnsServer) {
        setDnsServerUseCase(server)
        refreshServers()

        backCallback.isEnabled = (!vpnConnectionManager.connectionStatus.value.isDisconnected()
                && !lastChosenDnsServerIp.equals(appSettingsRepository.selectedDnsServerIP))

        lastChosenDnsServerIp = server.primaryIp
    }

    override fun onChooseDnsServerClick(server: DnsServer) {
        setDnsServerUseCase(server)
        refreshServers()

        backCallback.isEnabled = (!vpnConnectionManager.connectionStatus.value.isDisconnected()
                && !lastChosenDnsServerIp.equals(appSettingsRepository.selectedDnsServerIP))

        lastChosenDnsServerIp = server.primaryIp
    }

    override fun onDeleteDnsServerClick(server: DnsServer) {
        coroutineScope.launch(Dispatchers.Default) {
            deleteDnsServerUseCase(server)
                .onSuccess { refreshServers() }
        }
    }

    private fun showReconnectDialog() {
        dialogNavigation.activate(
            SlotConfig.AlertDialog(
                DialogMessage.ReconnectVPN,
                onConfirm = {
                    vpnConnectionManager.reconnectToLatestServer()
                    dialogNavigation.dismiss()
                    backCallback.isEnabled = false
                },
                onDismiss = {
                    dialogNavigation.dismiss()
                    backCallback.isEnabled = false
                })
        )
    }

    private fun refreshServers() {
        coroutineScope.launch {
            val selectedServerIP = appSettingsRepository.selectedDnsServerIP

            dnsServersRepository.getAllDnsServers()
                .onSuccess {
                    val allServers = defaultDnsServers + it
                    _state.value = _state.value.copy(
                        servers = it,
                        selectedServer = allServers.firstOrNull { dns ->
                            dns.primaryIp == selectedServerIP
                        },
                        isLoading = false
                    )
                }
                .onFailure { _state.value = _state.value.copy(isLoading = false) }
        }
    }

    @Serializable
    sealed interface SlotConfig {

        @Serializable
        data object CreateDnsDialog : SlotConfig

        @Serializable
        data class AlertDialog(
            val dialogMessage: DialogMessage,
            val onConfirm: () -> Unit,
            val onDismiss: () -> Unit
        ) : SlotConfig
    }
}
