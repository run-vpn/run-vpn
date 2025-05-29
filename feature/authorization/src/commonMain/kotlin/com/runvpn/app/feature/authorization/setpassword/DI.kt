package com.runvpn.app.feature.authorization.setpassword

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get

fun DecomposeComponentFactory.createSetPasswordComponent(
    componentContext: ComponentContext,
    onOutput: (SetPasswordComponent.Output) -> Unit,
): SetPasswordComponent {
    return DefaultSetPasswordComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        onOutput = onOutput
    )
}
