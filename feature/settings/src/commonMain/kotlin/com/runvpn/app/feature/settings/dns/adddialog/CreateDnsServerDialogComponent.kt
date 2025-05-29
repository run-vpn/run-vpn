package com.runvpn.app.feature.settings.dns.adddialog

import com.arkivanov.decompose.value.Value
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface CreateDnsServerDialogComponent : SimpleDialogComponent {

    data class State(
        val name: String,
        val ip: String,
        val isLoading: Boolean = false,
        val isNameError: Boolean = false,
        val isIpEmptyError: Boolean = false,
        val isIpFormatError: Boolean = false,
        val isCreatingError: Boolean = false,
        val isAlreadyCreatedError: Boolean = false
    )

    val state: Value<State>

    fun onAddServerClick()
    fun onServerNameChanged(newValue: String)
    fun onServerAddressChanged(newValue: String)
}
