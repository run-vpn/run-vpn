package com.runvpn.app.feature.authorization.confirmcode

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeConfirmCodeComponent : ConfirmCodeComponent {

    override val state: Value<ConfirmCodeComponent.State> = MutableValue(
        ConfirmCodeComponent.State(
            "",
            "testemail",
            isValidCode = false,
            isConfirmCodeError = false,
            isLoading = false,
            requestCodeTime = 59L
        )
    )

    override fun onConfirmClick() {
        TODO("Not yet implemented")
    }

    override fun onConfirmCodeChanged(code: String) {
        TODO("Not yet implemented")
    }

    override fun onChangeEmailClick() {
        TODO("Not yet implemented")
    }

    override fun onRequestNewCode() {
        TODO("Not yet implemented")
    }

    override fun onCancelClick() {
        TODO("Not yet implemented")
    }
}

