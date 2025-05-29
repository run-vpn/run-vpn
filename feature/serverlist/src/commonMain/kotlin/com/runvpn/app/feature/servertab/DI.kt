package com.runvpn.app.feature.servertab

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get


fun DecomposeComponentFactory.createServerTabComponent(
    componentContext: ComponentContext,
    output: (ServerTabComponent.Output) -> Unit
): ServerTabComponent {
    return DefaultServerTabComponent(componentContext, get(), onOutput = output)
}

