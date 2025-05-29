package com.runvpn.app.feature.settings

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.settings.common.CommonSettingsComponent
import com.runvpn.app.feature.settings.connection.ConnectionSettingsComponent
import com.runvpn.app.feature.settings.main.MainSettingsComponent
import com.runvpn.app.feature.settings.support.about.AboutComponent
import com.runvpn.app.feature.settings.support.main.SupportComponent

interface SettingsComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Main(val component: MainSettingsComponent) : Child
        data class Common(val component: CommonSettingsComponent) : Child
        data class Connection(val component: ConnectionSettingsComponent) : Child
        data class Support(val component: SupportComponent) : Child
        data class AboutApp(val component: AboutComponent) : Child
    }
}
