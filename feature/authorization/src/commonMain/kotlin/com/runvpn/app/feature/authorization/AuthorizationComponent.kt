package com.runvpn.app.feature.authorization

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.authorization.confirmcode.ConfirmCodeComponent
import com.runvpn.app.feature.authorization.email.EmailComponent
import com.runvpn.app.feature.authorization.enterpassword.EnterPasswordComponent
import com.runvpn.app.feature.authorization.setpassword.SetPasswordComponent

interface AuthorizationComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Email(val emailComponent: EmailComponent) : Child
        data class EnterPassword(val component: EnterPasswordComponent) : Child
        data class ConfirmCode(val confirmCodeComponent: ConfirmCodeComponent) : Child
        data class SetPassword(val component: SetPasswordComponent) : Child
    }

    sealed interface Output {
        data object OnAuthorizationSuccess : Output
        data object OnCancel : Output
    }
}

