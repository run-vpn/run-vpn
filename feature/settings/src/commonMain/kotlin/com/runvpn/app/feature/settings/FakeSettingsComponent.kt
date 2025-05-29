package com.runvpn.app.feature.settings

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

class FakeSettingsComponent : SettingsComponent {

    override val childStack: Value<ChildStack<*, SettingsComponent.Child>>
        get() = TODO("Not yet implemented")
}
