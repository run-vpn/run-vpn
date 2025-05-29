package com.runvpn.app.presentation.main

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get


fun DecomposeComponentFactory.createMainComponent(
    componentContext: ComponentContext,
    output: (MainComponent.Output) -> Unit
): MainComponent {
    return DefaultMainComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        output
    )
}

