package com.runvpn.app.tea.decompose.dialogs.confirmation

import com.arkivanov.decompose.ComponentContext

internal class DefaultConfirmationDialogComponent(
    componentContext: ComponentContext,
    override val message: String,
    override val positiveButtonLabel: String,
    override val negativeButtonLabel: String,
    private val positiveClicked: () -> Unit,
    private val negativeClicked: () -> Unit
) : ComponentContext by componentContext, ConfirmationDialogComponent {

    override fun onPositiveClicked() {
        positiveClicked()
    }

    override fun onNegativeClicked() {
        negativeClicked()
    }

    override fun onDismiss() {}
}

