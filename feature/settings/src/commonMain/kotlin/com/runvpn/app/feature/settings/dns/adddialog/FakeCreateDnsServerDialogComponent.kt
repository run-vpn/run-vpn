package com.runvpn.app.feature.settings.dns.adddialog

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeCreateDnsServerDialogComponent : CreateDnsServerDialogComponent {
    override val state: Value<CreateDnsServerDialogComponent.State> = MutableValue(
        CreateDnsServerDialogComponent.State(
            name = "",
            ip = ""
        )
    )

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }

    override fun onAddServerClick() {
        TODO("Not yet implemented")
    }

    override fun onServerNameChanged(newValue: String) {
        TODO("Not yet implemented")
    }

    override fun onServerAddressChanged(newValue: String) {
        TODO("Not yet implemented")
    }
}
