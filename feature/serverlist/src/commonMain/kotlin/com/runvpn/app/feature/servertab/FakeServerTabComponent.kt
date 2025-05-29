package com.runvpn.app.feature.servertab

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

class FakeServerTabComponent: ServerTabComponent {
    override val childStack: Value<ChildStack<*, ServerTabComponent.Child>>
        get() = TODO("Not yet implemented")

}

