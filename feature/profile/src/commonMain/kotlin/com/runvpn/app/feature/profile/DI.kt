package com.runvpn.app.feature.profile

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.feature.profile.main.DefaultProfileComponent
import com.runvpn.app.feature.profile.main.ProfileComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get


fun DecomposeComponentFactory.createProfileTabComponent(
    componentContext: ComponentContext
): ProfileTabComponent {
    return DefaultProfileTabComponent(componentContext, get(), get(), get())
}

fun DecomposeComponentFactory.createProfilesComponent(
    componentContext: ComponentContext, onOutput: (ProfileComponent.Output) -> Unit
): ProfileComponent {
    return DefaultProfileComponent(
        componentContext, get(), get(), get(), get(), get(), get(), get(), get(), get(), onOutput
    )
}

