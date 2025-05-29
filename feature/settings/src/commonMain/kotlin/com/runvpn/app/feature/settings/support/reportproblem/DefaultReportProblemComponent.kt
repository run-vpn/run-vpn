package com.runvpn.app.feature.settings.support.reportproblem

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.domain.usecases.ValidateEmailUseCase
import com.runvpn.app.tea.navigation.RootRouter

internal class DefaultReportProblemComponent(
    componentContext: ComponentContext,
    private val rootRouter: RootRouter,
    private val validateEmailUseCase: ValidateEmailUseCase
) : ReportProblemComponent, ComponentContext by componentContext {

    private val _state =
        MutableValue(ReportProblemComponent.State(email = "", isEmailValid = false, message = ""))
    override val state: Value<ReportProblemComponent.State>
        get() = _state

    override fun onEmailChange(email: String) {
        _state.value = state.value.copy(email = email, isEmailValid = validateEmailUseCase(email))
    }

    override fun onMessageChange(message: String) {
        _state.value = state.value.copy(message = message)
    }

    override fun onSendClick() {
        // Send report
        rootRouter.pop()
    }

    override fun onBack() {
        rootRouter.pop()
    }


}

