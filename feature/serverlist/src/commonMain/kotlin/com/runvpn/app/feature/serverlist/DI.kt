package com.runvpn.app.feature.serverlist

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get


fun DecomposeComponentFactory.createServerListComponent(
    componentContext: ComponentContext,
    output: (ServerListComponent.Output) -> Unit
): ServerListComponent {
    return DefaultServerListComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        output = output
    )
}
