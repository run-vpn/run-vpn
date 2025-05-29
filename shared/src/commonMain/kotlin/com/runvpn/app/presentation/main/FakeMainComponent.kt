package com.runvpn.app.presentation.main

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.home.FakeHomeComponent

class FakeMainComponent : MainComponent {

    override val activeChild: Value<ChildStack<*, MainComponent.Child>> = MutableValue(
        ChildStack(configuration = "string", MainComponent.Child.Home(FakeHomeComponent()))
    )

    override val state: Value<MainComponent.State>
        get() = TODO("Not yet implemented")

    override fun onHomeClicked() {
        TODO("Not yet implemented")
    }

    override fun onServersClicked() {
        TODO("Not yet implemented")
    }

    override fun onProfileClicked() {
        TODO("Not yet implemented")
    }

    override fun onSettingsClicked() {
        TODO("Not yet implemented")
    }
}
