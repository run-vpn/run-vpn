package com.runvpn.app.feature.authorization

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.authorization.confirmcode.ConfirmCodeComponent
import com.runvpn.app.feature.authorization.confirmcode.createConfirmCodeComponent
import com.runvpn.app.feature.authorization.email.EmailComponent
import com.runvpn.app.feature.authorization.email.createEmailComponent
import com.runvpn.app.feature.authorization.enterpassword.EnterPasswordComponent
import com.runvpn.app.feature.authorization.setpassword.SetPasswordComponent
import com.runvpn.app.feature.authorization.setpassword.createSetPasswordComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import kotlinx.serialization.Serializable

class DefaultAuthorizationComponent(
    componentContext: ComponentContext,
    private val decomposeComponentFactory: DecomposeComponentFactory,
    private val onOutput: (AuthorizationComponent.Output) -> Unit
) : AuthorizationComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()

    private var userEmail: String? = null

    override val childStack: Value<ChildStack<*, AuthorizationComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.Email,
        key = "",
        childFactory = ::createChild,
        handleBackButton = true
    )

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): AuthorizationComponent.Child =
        when (config) {

            is ChildConfig.Email -> AuthorizationComponent.Child.Email(
                decomposeComponentFactory.createEmailComponent(
                    componentContext,
                    ::handleEmailChildOutput
                )
            )

            is ChildConfig.ConfirmCode -> AuthorizationComponent.Child.ConfirmCode(
                decomposeComponentFactory.createConfirmCodeComponent(
                    componentContext,
                    config.email,
                    ::onCodeConfirmed
                )
            )

            is ChildConfig.EnterPassword -> AuthorizationComponent.Child.EnterPassword(
                decomposeComponentFactory.createEnterPasswordComponent(
                    componentContext,
                    forEmail = config.email,
                    output = ::handleEnterPasswordOutput
                )
            )

            is ChildConfig.SetPassword -> AuthorizationComponent.Child.SetPassword(
                decomposeComponentFactory.createSetPasswordComponent(
                    componentContext,
                    ::handleSetPasswordOutput
                )
            )
        }

    private fun handleSetPasswordOutput(output: SetPasswordComponent.Output) {
        when (output) {
            SetPasswordComponent.Output.PasswordGenerated ->
                onOutput(AuthorizationComponent.Output.OnAuthorizationSuccess)

            SetPasswordComponent.Output.PasswordSaved ->
                onOutput(AuthorizationComponent.Output.OnAuthorizationSuccess)

            SetPasswordComponent.Output.OnBack -> navigation.pop()
        }
    }


    private fun handleEnterPasswordOutput(output: EnterPasswordComponent.Output) {
        userEmail?.let { email ->
            when (output) {
                EnterPasswordComponent.Output.OnAuthByCodeRequested -> navigation.push(
                    ChildConfig.ConfirmCode(
                        email
                    )
                )

                EnterPasswordComponent.Output.OnSuccessfulAuthorized -> onOutput.invoke(
                    AuthorizationComponent.Output.OnAuthorizationSuccess
                )

                EnterPasswordComponent.Output.OnBack -> navigation.pop()
            }
        }
    }

    private fun handleEmailChildOutput(output: EmailComponent.Output) {
        when (output) {
            is EmailComponent.Output.OnBack -> onOutput.invoke(AuthorizationComponent.Output.OnCancel)
            is EmailComponent.Output.OnEmailConfirmed -> {
                userEmail = output.email

                navigation.push(
                    ChildConfig.EnterPassword(output.email)
                )
            }
        }
    }

    private fun onCodeConfirmed(output: ConfirmCodeComponent.Output) {
        when (output) {

            is ConfirmCodeComponent.Output.OnCodeConfirmed -> {
                onOutput.invoke(AuthorizationComponent.Output.OnAuthorizationSuccess)
            }

            ConfirmCodeComponent.Output.ChangeEmailRequested -> navigation.popWhile {
                it !is ChildConfig.Email
            }

            is ConfirmCodeComponent.Output.OnSetPasswordRequested -> {
                navigation.push(ChildConfig.SetPassword)
            }

            is ConfirmCodeComponent.Output.OnBack -> {
                navigation.pop()
            }
        }
    }


    @Serializable
    private sealed interface ChildConfig {

        @Serializable
        data object Email : ChildConfig

        @Serializable
        data class ConfirmCode(val email: String) : ChildConfig

        @Serializable
        data class EnterPassword(val email: String) : ChildConfig

        @Serializable
        data object SetPassword : ChildConfig
    }

}

