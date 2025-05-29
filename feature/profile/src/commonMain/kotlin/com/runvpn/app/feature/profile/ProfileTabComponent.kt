package com.runvpn.app.feature.profile

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.profile.main.ProfileComponent

interface ProfileTabComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Main(val profileComponent: ProfileComponent) : Child
    }

    sealed interface Output {
        data object OnAuthScreenRequested : Output
        data object OnTrafficModuleRequested : Output
    }
}

