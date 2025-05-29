package com.runvpn.app.android.screens.servers.serveradd.wireguard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.runvpn.app.android.ext.getFileName
import com.runvpn.app.android.ext.openAppSettings
import com.runvpn.app.android.ext.readFile
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.CommonAlertDialog
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.feature.serveradd.wireguard.DefaultWireGuardConfigComponent
import com.runvpn.app.feature.serveradd.wireguard.FakeWireGuardConfigComponent
import com.runvpn.app.feature.serveradd.wireguard.WireGuardConfigComponent
import com.runvpn.app.tea.dialog.DialogMessage
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig

@Composable
fun WireGuardServerConfigForm(component: WireGuardConfigComponent, modifier: Modifier = Modifier) {

    val state by component.state.subscribeAsState()

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.getFileName(uri)?.let { fileName ->
            if (fileName.split(".")
                    .last() == DefaultWireGuardConfigComponent.WIREGUARD_FILE_EXTENSION
            ) {
                context.readFile(uri)?.let { config ->
                    val wgConfig = createWgConfig(config)
                    component.onLoadWgConfig(wgConfig)
                }
                return@rememberLauncherForActivityResult
            }
        }
    }

    var openCameraPermissionRationaleDialog by remember {
        mutableStateOf(false)
    }

    val qrCodeScannerLauncher =
        rememberLauncherForActivityResult(contract = ScanCustomCode(), onResult = { qrResult ->
            when (qrResult) {
                is QRResult.QRSuccess -> {
                    val wgConfig = createWgConfig(qrResult.content.rawValue)
                    component.onLoadWgConfig(wgConfig)
                }

                is QRResult.QRMissingPermission -> {
                    openCameraPermissionRationaleDialog = true
                }

                else -> {}
            }
        })


    Column(modifier = modifier.padding(horizontal = 16.dp)) {

        AppButton(
            onClick = {
                qrCodeScannerLauncher.launch(ScannerConfig.build {
                    setHapticSuccessFeedback(true)
                    // enable (default) or disable haptic feedback when a barcode was detected
                    setShowTorchToggle(true)
                    // show or hide (default) torch/flashlight toggle button
                    setShowCloseButton(true)
                    // show or hide (default) close button
                })
            }, modifier = Modifier.fillMaxWidth()
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
            onClick = { launcher.launch("application/octet-stream") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_tint),
                contentDescription = null,
                tint = textWhite
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = R.string.choose_config_file),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column {

            Text(text = stringResource(R.string.wireguard_interface), fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = state.privateKey,
                placeholder = stringResource(R.string.private_key),
                onValueChanged = component::onPrivateKeyChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = state.publicKey,
                placeholder = stringResource(R.string.public_key_generated),
                onValueChanged = component::onPublicKeyChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = state.ipAddress,
                placeholder = stringResource(R.string.address),
                onValueChanged = component::onIpAddressChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = state.port,
                placeholder = stringResource(R.string.port),
                onValueChanged = component::onPortChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = state.dnsServers,
                placeholder = stringResource(R.string.dnsservers),
                onValueChanged = component::onDnsServersChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = state.mtu,
                placeholder = stringResource(R.string.mtu),
                onValueChanged = component::onMtuChange,
                modifier = Modifier.fillMaxWidth()
            )
        }


        Spacer(modifier = Modifier.height(4.dp))

        Column {
            state.peers.forEachIndexed { index, item ->
                WireGuardPeerView(component = component, index, item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppButton(
            onClick = { component.onAddPeer() }, modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_tint),
                contentDescription = null,
                tint = textWhite
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(R.string.add_peer),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
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


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WireGuardServerConfigFormPreview() {
    WireGuardServerConfigForm(component = FakeWireGuardConfigComponent())
}
