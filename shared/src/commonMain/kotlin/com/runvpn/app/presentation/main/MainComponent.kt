package com.runvpn.app.presentation.main

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.home.HomeComponent
import com.runvpn.app.feature.profile.ProfileTabComponent
import com.runvpn.app.feature.servertab.ServerTabComponent
import com.runvpn.app.feature.settings.SettingsComponent

interface MainComponent {

    val activeChild: Value<ChildStack<*, Child>>

    val state: Value<State>

    fun onHomeClicked()
    fun onServersClicked()
    fun onProfileClicked()
    fun onSettingsClicked()

    data class State(
        val hasUpdate: Boolean
    )

    sealed interface Child {
        data class Home(val component: HomeComponent) : Child
        data class Servers(val component: ServerTabComponent) : Child
        data class Profile(val component: ProfileTabComponent) : Child
        data class Settings(val component: SettingsComponent) : Child
    }

    sealed interface Output {
        data object OnAuthScreenRequest : Output
        data object OnAddServerScreenRequest : Output
        data object OnMyServersScreenRequest : Output
        data object OnAboutScreenRequest : Output
    }
}

