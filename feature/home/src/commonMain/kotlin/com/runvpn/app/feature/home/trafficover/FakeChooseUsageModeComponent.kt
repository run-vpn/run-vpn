package com.runvpn.app.feature.home.trafficover

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.AppUseMode

class FakeChooseUsageModeComponent : ChooseUsageModeComponent {
    override val state: Value<ChooseUsageModeComponent.State>
        get() = MutableValue(ChooseUsageModeComponent.State(selectedUseMode = AppUseMode.FREE))

    override fun onUsageModeClick(useMode: AppUseMode) {
        TODO("Not yet implemented")
    }

    override fun onConfirmClick() {
        TODO("Not yet implemented")
    }

    override fun onReadFullTermsClick() {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
