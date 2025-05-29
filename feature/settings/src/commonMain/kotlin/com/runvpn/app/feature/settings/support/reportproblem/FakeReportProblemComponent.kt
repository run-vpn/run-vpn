package com.runvpn.app.feature.settings.support.reportproblem

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeReportProblemComponent : ReportProblemComponent {
    override val state: Value<ReportProblemComponent.State>
        get() = MutableValue(
            ReportProblemComponent.State(
                email = "testEmail@sdas.asd",
                isEmailValid = false,
                message = "Test Message"
            )
        )

    override fun onEmailChange(email: String) {
        TODO("Not yet implemented")
    }

    override fun onMessageChange(message: String) {
        TODO("Not yet implemented")
    }

    override fun onSendClick() {
        TODO("Not yet implemented")
    }

    override fun onBack() {
        TODO("Not yet implemented")
    }

}

