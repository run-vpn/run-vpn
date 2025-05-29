package com.runvpn.app.feature.trafficmodule

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get

actual fun DecomposeComponentFactory.createTrafficModuleComponent(
    componentContext: ComponentContext,
    onOutput: (TrafficModuleComponent.Output) -> Unit
): TrafficModuleComponent {
    return DefaultTrafficModuleComponent(
        componentContext,
        get(),
        get(),
//        get(),
//        get(),
        onOutput
    )
}
