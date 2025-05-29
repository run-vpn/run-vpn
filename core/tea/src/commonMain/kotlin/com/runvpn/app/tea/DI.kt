package com.runvpn.app.tea

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.dialogs.alert.AlertDialogComponent
import com.runvpn.app.tea.decompose.dialogs.alert.DefaultAlertDialogComponent
import com.runvpn.app.tea.decompose.dialogs.confirmation.ConfirmationDialogComponent
import com.runvpn.app.tea.decompose.dialogs.confirmation.DefaultConfirmationDialogComponent
import com.runvpn.app.tea.dialog.DialogMessage
import com.runvpn.app.tea.message.data.DefaultMessageService
import com.runvpn.app.tea.message.data.MessageService
import org.koin.dsl.module

val coreTeaModule = module {
    single<MessageService> { DefaultMessageService() }
}

fun DecomposeComponentFactory.createCommonDialog(
    componentContext: ComponentContext,
    dialogMessage: DialogMessage,
    onConfirm: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
): AlertDialogComponent {
    return DefaultAlertDialogComponent(
        componentContext = componentContext,
        dialogMessage = dialogMessage,
        confirmAction = onConfirm,
        dismiss = onDismiss
    )
}

fun DecomposeComponentFactory.createConfirmationDialogComponent(
    componentContext: ComponentContext,
    message: String,
    positiveButtonLabel: String,
    negativeButtonLabel: String,
    onPositiveClicked: () -> Unit,
    onNegativeClicked: () -> Unit
): ConfirmationDialogComponent {
    return DefaultConfirmationDialogComponent(
        componentContext = componentContext,
        message = message,
        positiveButtonLabel = positiveButtonLabel,
        negativeButtonLabel = negativeButtonLabel,
        positiveClicked = onPositiveClicked,
        negativeClicked = onNegativeClicked
    )
}

