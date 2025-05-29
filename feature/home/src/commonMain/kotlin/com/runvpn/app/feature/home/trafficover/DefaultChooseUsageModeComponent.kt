package com.runvpn.app.feature.home.trafficover

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.runvpn.app.data.settings.domain.AppUseMode
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class DefaultChooseUsageModeComponent(
    componentContext: ComponentContext,
    private val appSettingsRepository: AppSettingsRepository,
    isCancellable: Boolean,
    private val onDismiss: () -> Unit
) : ChooseUsageModeComponent, ComponentContext by componentContext {

    private val _state =
        MutableValue(ChooseUsageModeComponent.State(selectedUseMode = appSettingsRepository.appUsageMode))

    override val state: Value<ChooseUsageModeComponent.State> = _state

    private val backCallback = BackCallback { onDismiss() }

    init { if (isCancellable) backHandler.register(backCallback) }

    override fun onUsageModeClick(useMode: AppUseMode) {
        _state.value = state.value.copy(selectedUseMode = useMode)
        appSettingsRepository.appUsageMode = useMode
    }

    override fun onConfirmClick() {
        appSettingsRepository.isPrivacyPolicyAccepted = true
        onDismiss()
    }

    override fun onReadFullTermsClick() {
        // read full terms logics here (open web etc..)
    }

    override fun onDismissClicked() {
        onDismiss()
    }
}
