package com.runvpn.app.feature.settings.dns.adddialog

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.data.connection.domain.DnsServer
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get


fun DecomposeComponentFactory.createCreateDnsServerDialog(
    componentContext: ComponentContext,
    onDismiss: () -> Unit,
    onCompleted: (DnsServer) -> Unit
): CreateDnsServerDialogComponent {
    return DefaultCreateDnsServerDialogComponent(
        componentContext = componentContext,
        dnsServersRepository = get(),
        exceptionHandler = get(),
        checkIPv4ValidUseCase = get(),
        checkIPv6ValidUseCase = get(),
        addDnsServerUseCase = get(),
        onCompleted = onCompleted,
        onDismiss = onDismiss
    )
}
