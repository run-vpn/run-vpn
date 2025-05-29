package com.runvpn.app.feature.profile

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

class FakeProfileTabComponent : ProfileTabComponent {
    override val childStack: Value<ChildStack<*, ProfileTabComponent.Child>>
        get() = TODO("Not yet implemented")

}

