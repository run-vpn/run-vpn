package com.runvpn.app.feature.home.welcome

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.AppUseMode

class FakeWelcomeComponent : WelcomeComponent {
    override val state: Value<WelcomeComponent.State>
        get() = MutableValue(WelcomeComponent.State(selectedUseMode = AppUseMode.PAID, false))

    override fun onUsageModeClick(useMode: AppUseMode) {
        TODO("Not yet implemented")
    }

    override fun onConfirmClick() {
        TODO("Not yet implemented")
    }
}
