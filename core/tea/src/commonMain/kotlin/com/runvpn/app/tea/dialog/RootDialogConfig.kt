package com.runvpn.app.tea.dialog

import kotlinx.serialization.Serializable

@Serializable
sealed interface RootDialogConfig {

    @Serializable
    data object ForceUpdateConfig : RootDialogConfig

    @Serializable
    data class ChooseAppUsageModeConfig(val isCancellable: Boolean) : RootDialogConfig

    @Serializable
    data object Support : RootDialogConfig

    @Serializable
    data class AlertDialog(
        val dialogMessage: DialogMessage,
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit
    ) : RootDialogConfig

}
