package com.runvpn.app.tea.message

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.message.presentation.DefaultMessageComponent
import com.runvpn.app.tea.message.presentation.MessageComponent
import org.koin.core.component.get

fun DecomposeComponentFactory.createMessageComponent(
    componentContext: ComponentContext
): MessageComponent {
    return DefaultMessageComponent(componentContext, get())
}
