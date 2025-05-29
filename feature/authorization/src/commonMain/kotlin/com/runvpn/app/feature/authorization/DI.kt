package com.runvpn.app.feature.authorization

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.feature.authorization.enterpassword.DefaultEnterPasswordComponent
import com.runvpn.app.feature.authorization.enterpassword.EnterPasswordComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get


fun DecomposeComponentFactory.createAuthorizationComponent(
    componentContext: ComponentContext, onOutput: (AuthorizationComponent.Output) -> Unit
): AuthorizationComponent {
    return DefaultAuthorizationComponent(componentContext, get(), onOutput = onOutput)
}

fun DecomposeComponentFactory.createEnterPasswordComponent(
    componentContext: ComponentContext,
    forEmail: String,
    output: (EnterPasswordComponent.Output) -> Unit
): EnterPasswordComponent {
    return DefaultEnterPasswordComponent(
        componentContext = componentContext,
        exceptionHandler = get(),
        email = forEmail,
        authByEmailPasswordUseCase = get(),
        sendEmailConfirmationCodeUseCase = get(),
        messageService = get(),
        validatePasswordUseCase = get(),
        output = output
    )
}
