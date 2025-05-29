package com.runvpn.app.feature.authorization.email

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeEmailComponent : EmailComponent {
    override val state: Value<EmailComponent.State>
        get() = MutableValue(
            EmailComponent.State(
                email = "",
                isEmailValid = false,
                isLoading = true
            )
        )

    override fun onConfirmEmailClick() {
        TODO("Not yet implemented")
    }

    override fun onEmailChanged(email: String) {
        TODO("Not yet implemented")
    }

    override fun onAuthByGoogleClick() {
        TODO("Not yet implemented")
    }

    override fun onAuthByFacebookClick() {
        TODO("Not yet implemented")
    }

    override fun onAuthByTelegramClick() {
        TODO("Not yet implemented")
    }

    override fun onCancelClick() {
        TODO("Not yet implemented")
    }
}

