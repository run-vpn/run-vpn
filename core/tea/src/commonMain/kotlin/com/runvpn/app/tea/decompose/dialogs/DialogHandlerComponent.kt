package com.runvpn.app.tea.decompose.dialogs

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value

interface DialogHandlerComponent {

    val dialogChildOverlay: Value<ChildSlot<*, DialogComponent>>

    fun showDialog(dialog: DialogComponent)

    fun dismiss()
}

