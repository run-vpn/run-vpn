package com.runvpn.app.feature.home.welcome

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.runvpn.app.data.settings.domain.AppUseMode
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.tea.navigation.RootRouter

class DefaultWelcomeComponent(
    componentContext: ComponentContext,
    private val appSettingsRepository: AppSettingsRepository,
    private val rootRouter: RootRouter,
    private val hasBackStack: Boolean,
    private val output: (WelcomeComponent.Output) -> Unit
) : WelcomeComponent, ComponentContext by componentContext {

    private val _state =
        MutableValue(
            WelcomeComponent.State(
                selectedUseMode = appSettingsRepository.appUsageMode,
                hasCloseButton = hasBackStack
            )
        )

    override val state: Value<WelcomeComponent.State>
        get() = _state

    private val backCallback = BackCallback {
        output(WelcomeComponent.Output.OnConfirm(hasBackStack = hasBackStack))
    }

    init {
        if (hasBackStack) backHandler.register(backCallback)
    }

    override fun onUsageModeClick(useMode: AppUseMode) {
        _state.value = state.value.copy(selectedUseMode = useMode)
        appSettingsRepository.appUsageMode = useMode
    }

    override fun onConfirmClick() {
        appSettingsRepository.isPrivacyPolicyAccepted = true
        output(WelcomeComponent.Output.OnConfirm(hasBackStack = hasBackStack))
    }
}
