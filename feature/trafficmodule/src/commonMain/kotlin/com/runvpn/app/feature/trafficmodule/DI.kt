package com.runvpn.app.feature.trafficmodule

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory


expect fun DecomposeComponentFactory.createTrafficModuleComponent(
    componentContext: ComponentContext,
    onOutput: (TrafficModuleComponent.Output) -> Unit
): TrafficModuleComponent
