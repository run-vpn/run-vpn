package com.runvpn.app.feature.authorization.confirmcode

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get

fun DecomposeComponentFactory.createConfirmCodeComponent(
    componentContext: ComponentContext,
    email: String,
    onOutput: (ConfirmCodeComponent.Output) -> Unit
): ConfirmCodeComponent {
    return DefaultConfirmCodeComponent(
        componentContext,
        get(),
        get(),
        get(),
        email,
        onOutput
    )
}

