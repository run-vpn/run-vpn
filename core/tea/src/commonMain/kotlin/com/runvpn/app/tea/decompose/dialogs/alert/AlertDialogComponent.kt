package com.runvpn.app.tea.decompose.dialogs.alert

import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import com.runvpn.app.tea.dialog.DialogMessage

interface AlertDialogComponent : SimpleDialogComponent {

    val dialogMessage: DialogMessage

    fun onConfirm()
}

