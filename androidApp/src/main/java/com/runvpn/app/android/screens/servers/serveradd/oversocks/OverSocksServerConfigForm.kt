package com.runvpn.app.android.screens.servers.serveradd.oversocks

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.openAppSettings
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.CommonAlertDialog
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.feature.serveradd.oversocks.FakeOverSocksConfigComponent
import com.runvpn.app.feature.serveradd.oversocks.OverSocksConfigComponent
import com.runvpn.app.tea.dialog.DialogMessage
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig

@Composable
fun OverSocksServerConfigForm(component: OverSocksConfigComponent, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val state by component.state.subscribeAsState()

    var openCameraPermissionRationaleDialog by remember {
        mutableStateOf(false)
    }

    val qrCodeScannerLauncher = rememberLauncherForActivityResult(
        contract = ScanCustomCode(),
        onResult = { qrResult ->
            when (qrResult) {
                is QRResult.QRSuccess -> {
                    component.onLoadConfig(qrResult.content.rawValue)
                }

                is QRResult.QRMissingPermission -> {
                    openCameraPermissionRationaleDialog = true
                }

                else -> {}
            }
        })

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppButton(
            onClick = {
                qrCodeScannerLauncher.launch(
                    ScannerConfig.build {
                        setHapticSuccessFeedback(true)
                        // enable (default) or disable haptic feedback when a barcode was detected
                        setShowTorchToggle(true)
                        // show or hide (default) torch/flashlight toggle button
                        setShowCloseButton(true)
                        // show or hide (default) close button
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_tint),
                contentDescription = null,
                tint = textWhite
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = R.string.qr_code),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AppButton(
            onClick = component::onPasteClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_tint),
                contentDescription = null,
                tint = textWhite
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = R.string.paste_from_clipboard),
                textAlign = TextAlign.Center,
                fontSize = 16.sp, fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.host,
            placeholder = stringResource(R.string.address),
            onValueChanged = component::onHostChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = state.port,
            placeholder = stringResource(R.string.port),
            onValueChanged = component::onPortChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = state.username,
            placeholder = stringResource(R.string.username),
            onValueChanged = component::onUserNameChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = state.password,
            placeholder = stringResource(R.string.server_config_password),
            onValueChanged = component::onPasswordChange,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (openCameraPermissionRationaleDialog) {
        CommonAlertDialog(
            onDismissRequest = { openCameraPermissionRationaleDialog = false },
            onConfirmation = {
                context.openAppSettings()
                openCameraPermissionRationaleDialog = false
            },
            dialogMessage = DialogMessage.Common(
                title = stringResource(id = R.string.permission_notifications_rationale_title),
                message = stringResource(id = R.string.permission_camera_rationale_desc),
                positiveButtonText = stringResource(id = R.string.open_settings)
            ),
            icon = Icons.Default.Info
        )
    }
}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun OverSocksServerConfigFormPreview() {
    OverSocksServerConfigForm(component = FakeOverSocksConfigComponent())
}



