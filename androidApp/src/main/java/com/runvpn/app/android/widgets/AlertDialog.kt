package com.runvpn.app.android.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.getLocalizedMessage
import com.runvpn.app.tea.dialog.DialogMessage

@Composable
fun CommonAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogMessage: DialogMessage,
    icon: ImageVector
) {
    val localizedDialogMessage = dialogMessage.getLocalizedMessage(LocalContext.current)

    AlertDialog(
        icon = { Icon(icon, contentDescription = null) },
        title = { Text(text = localizedDialogMessage.title) },
        text = { Text(text = localizedDialogMessage.message) },
        onDismissRequest = { onDismissRequest() },
        dismissButton = {
            localizedDialogMessage.negativeButtonText?.let {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(text = it)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text(text = localizedDialogMessage.positiveButtonText)
            }
        }
    )
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CommonAlertDialogPreview() {
    RunVpnTheme {
        CommonAlertDialog(
            onDismissRequest = { /*TODO*/ },
            onConfirmation = { /*TODO*/ },
            dialogMessage = DialogMessage.Common(
                "Title",
                message = "Message Message Message Message Message Message Message Message Message",
                negativeButtonText = "NegativeButton",
                positiveButtonText = "PositiveButton"
            ),
            icon = Icons.Default.Info
        )
    }
}
