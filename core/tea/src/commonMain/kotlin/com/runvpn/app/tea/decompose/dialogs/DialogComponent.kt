package com.runvpn.app.tea.decompose.dialogs

interface DialogComponent {
    val message: String

    fun onDismiss()
}
