package com.runvpn.app.feature.myservers

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get

fun DecomposeComponentFactory.createMyServersComponent(
    componentContext: ComponentContext,
    output: (MyServersComponent.Output) -> Unit
): MyServersComponent {
    return DefaultMyServersComponent(componentContext, get(),get(), get(), output)
}
