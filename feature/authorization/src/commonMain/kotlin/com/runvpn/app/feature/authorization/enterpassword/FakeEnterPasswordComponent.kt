package com.runvpn.app.feature.authorization.enterpassword

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.PasswordValidationResult

class FakeEnterPasswordComponent : EnterPasswordComponent {

    override val state: Value<EnterPasswordComponent.State> = MutableValue(
        EnterPasswordComponent.State(
            password = "",
            passwordValidationResult = PasswordValidationResult.ErrorMinimumSymbols
        )
    )

    override fun onPasswordChanged(value: String) {
        TODO("Not yet implemented")
    }

    override fun onContinueClick() {
        TODO("Not yet implemented")
    }

    override fun onAuthByEmailCodeClick() {
        TODO("Not yet implemented")
    }

    override fun onForgotPasswordClick() {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }
}
