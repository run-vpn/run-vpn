package com.runvpn.app.feature.settings.dns

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.data.defaultDnsServers
import com.runvpn.app.data.connection.domain.DnsServer
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

class FakeChooseDnsServerComponent(
    private val isEmptyMyServerList: Boolean = false
) : ChooseDnsServerComponent {

    override val state: Value<ChooseDnsServerComponent.State> = MutableValue(
        ChooseDnsServerComponent.State(
            defaultServers = defaultDnsServers,
            servers = if (!isEmptyMyServerList) {
                listOf(
                    DnsServer.createEmpty("Google", "8.8.4.4").copy(id = 100),
                    DnsServer.createEmpty("Cloudflare", "1.1.1.1").copy(id = 101),
                    DnsServer.createEmpty("Yandex DNS", "5.5.3.3").copy(id = 102),
                )
            } else listOf(),
            selectedServer = null,
            isLoading = false
        )
    )
    override val childSlot: Value<ChildSlot<*, SimpleDialogComponent>> = MutableValue(
        ChildSlot<Nothing, Nothing>(null)
    )

    override fun onAddServerClick() {
        TODO("Not yet implemented")
    }

    override fun onDnsServerClick(server: DnsServer) {
        TODO("Not yet implemented")
    }

    override fun onChooseDnsServerClick(server: DnsServer) {
        TODO("Not yet implemented")
    }

    override fun onDeleteDnsServerClick(server: DnsServer) {
        TODO("Not yet implemented")
    }
}
