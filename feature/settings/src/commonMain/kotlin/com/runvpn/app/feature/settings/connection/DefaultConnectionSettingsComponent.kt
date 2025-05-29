package com.runvpn.app.feature.settings.connection

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.tea.dialog.DialogManager
import com.runvpn.app.tea.dialog.DialogMessage
import com.runvpn.app.tea.dialog.RootDialogConfig

class DefaultConnectionSettingsComponent(
    componentContext: ComponentContext,
    private val appSettingsRepository: AppSettingsRepository,
    private val dialogManager: DialogManager,
    private val vpnConnectionManager: VpnConnectionManager,
    private val output: (ConnectionSettingsComponent.Output) -> Unit
) : ConnectionSettingsComponent, ComponentContext by componentContext {

    private val _state =
        MutableValue(ConnectionSettingsComponent.State(allowLanConnections = appSettingsRepository.allowLanConnection))
    override val state: Value<ConnectionSettingsComponent.State>
        get() = _state


    override fun onAllowLanConnectionsToggle(isEnabled: Boolean) {
        _state.value = state.value.copy(allowLanConnections = isEnabled)
        appSettingsRepository.allowLanConnection = isEnabled
        if (vpnConnectionManager.connectionStatus.value.isDisconnected().not()) {
            showReconnectDialog()
        }
    }

    override fun onSplitTunnellingClick() {
        output(ConnectionSettingsComponent.Output.OnSplitTunnellingScreenRequested)
    }

    override fun onDnsServersClick() {
        output(ConnectionSettingsComponent.Output.OnDnsServersScreenRequested)
    }

    private fun showReconnectDialog() {
        dialogManager.showDialog(
            RootDialogConfig.AlertDialog(
                DialogMessage.ReconnectVPN,
                onConfirm = {
                    vpnConnectionManager.reconnectToLatestServer()
                    dialogManager.dismiss()
                },
                onDismiss = {
                    dialogManager.dismiss()
                })
        )
    }

}
