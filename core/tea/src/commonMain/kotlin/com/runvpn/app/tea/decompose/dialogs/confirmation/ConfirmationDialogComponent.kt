package com.runvpn.app.tea.decompose.dialogs.confirmation

import com.runvpn.app.tea.decompose.dialogs.DialogComponent

interface ConfirmationDialogComponent : DialogComponent {
    val positiveButtonLabel: String
    val negativeButtonLabel: String

    fun onPositiveClicked()
    fun onNegativeClicked()
}

