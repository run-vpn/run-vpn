package com.runvpn.app.tea.decompose.dialogs.alert

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.dialog.DialogMessage

internal class DefaultAlertDialogComponent(
    componentContext: ComponentContext,
    override val dialogMessage: DialogMessage,
    private val confirmAction: (() -> Unit)? = null,
    private val dismiss: (() -> Unit)? = null,
) : ComponentContext by componentContext, AlertDialogComponent {

    override fun onConfirm() {
        confirmAction?.invoke()
    }

    override fun onDismissClicked() {
        dismiss?.invoke()
    }
}

