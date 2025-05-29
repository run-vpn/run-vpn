package com.runvpn.app.feature.authorization

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

class FakeAuthorizationComponent : AuthorizationComponent {

    override val childStack: Value<ChildStack<*, AuthorizationComponent.Child>> = TODO()
}
