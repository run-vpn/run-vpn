package com.runvpn.app.feature.settings.dns

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.domain.DnsServer
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface ChooseDnsServerComponent {

    data class State(
        val defaultServers: List<DnsServer>,
        val servers: List<DnsServer>,
        val selectedServer: DnsServer?,
        val isLoading: Boolean = false
    )

    val state: Value<State>

    val childSlot: Value<ChildSlot<*, SimpleDialogComponent>>

    fun onAddServerClick()
    fun onDnsServerClick(server: DnsServer)
    fun onChooseDnsServerClick(server: DnsServer)
    fun onDeleteDnsServerClick(server: DnsServer)
}
