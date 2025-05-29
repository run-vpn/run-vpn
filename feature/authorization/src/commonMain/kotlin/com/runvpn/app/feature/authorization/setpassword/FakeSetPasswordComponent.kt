package com.runvpn.app.feature.authorization.setpassword

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeSetPasswordComponent : SetPasswordComponent {

    override val state: Value<SetPasswordComponent.State> = MutableValue(
        SetPasswordComponent.State(
            password = "1234",
            confirmPassword = "",
            isFormValid = false,
            passwordValidationResult = null
        )
    )

    override fun onPasswordChanged(password: String) {
        TODO("Not yet implemented")
    }

    override fun onConfirmPasswordChanged(value: String) {
        TODO("Not yet implemented")
    }

    override fun onSavePasswordAndCloseClick() {
        TODO("Not yet implemented")
    }

    override fun onGeneratePasswordAndSendToEmailClick() {
        TODO("Not yet implemented")
    }

    override fun onCancelClick() {
        TODO("Not yet implemented")
    }
}
