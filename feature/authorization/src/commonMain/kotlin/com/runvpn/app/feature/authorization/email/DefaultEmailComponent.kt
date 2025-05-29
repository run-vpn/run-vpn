package com.runvpn.app.feature.authorization.email

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.domain.usecases.ValidateEmailUseCase
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage

class DefaultEmailComponent(
    componentContext: ComponentContext,
    private val onOutput: (EmailComponent.Output) -> Unit,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val messageService: MessageService
) : EmailComponent, ComponentContext by componentContext {


    private val _state =
        MutableValue(EmailComponent.State(email = "", isEmailValid = false, isLoading = false))
    override val state: Value<EmailComponent.State>
        get() = _state

    override fun onConfirmEmailClick() {
        onOutput.invoke(EmailComponent.Output.OnEmailConfirmed(state.value.email))
    }

    override fun onEmailChanged(email: String) {
        _state.value = state.value.copy(email = email, isEmailValid = validateEmailUseCase(email))
    }

    override fun onAuthByGoogleClick() {
        messageService.showMessage(AppMessage.NotImplemented())
    }

    override fun onAuthByFacebookClick() {
        messageService.showMessage(AppMessage.NotImplemented())
    }

    override fun onAuthByTelegramClick() {
        messageService.showMessage(AppMessage.NotImplemented())
    }

    override fun onCancelClick() {
        onOutput.invoke(EmailComponent.Output.OnBack)
    }

}

