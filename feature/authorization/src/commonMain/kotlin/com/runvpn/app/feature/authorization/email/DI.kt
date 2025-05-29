package com.runvpn.app.feature.authorization.email

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get

fun DecomposeComponentFactory.createEmailComponent(
    componentContext: ComponentContext,
    onOutput: (EmailComponent.Output) -> Unit,
): EmailComponent {
    return DefaultEmailComponent(
        componentContext,
        onOutput = onOutput,
        get(),
        get()
    )
}

