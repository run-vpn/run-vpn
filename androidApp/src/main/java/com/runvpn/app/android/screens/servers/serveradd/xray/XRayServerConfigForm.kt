package com.runvpn.app.android.screens.servers.serveradd.xray

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.openAppSettings
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.ClickableTextField
import com.runvpn.app.android.widgets.CommonAlertDialog
import com.runvpn.app.android.widgets.ItemListBottomSheet
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.data.servers.domain.entities.VlessFlow
import com.runvpn.app.data.servers.domain.entities.VlessNetworkType
import com.runvpn.app.data.servers.domain.entities.VlessSecurity
import com.runvpn.app.feature.serveradd.xray.FakeXrayVpnServerConfigComponent
import com.runvpn.app.feature.serveradd.xray.XrayVpnServerConfigComponent
import com.runvpn.app.tea.dialog.DialogMessage
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig

@Composable
fun XRayServerConfigForm(component: XrayVpnServerConfigComponent, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val state by component.state.subscribeAsState()

    var openCameraPermissionRationaleDialog by remember {
        mutableStateOf(false)
    }

    var showSelectNetworkBottomSheet by remember {
        mutableStateOf(false)
    }

    var showSelectSecurityBottomSheet by remember {
        mutableStateOf(false)
    }

    var showSelectFlowBottomSheet by remember {
        mutableStateOf(false)
    }

    val qrCodeScannerLauncher = rememberLauncherForActivityResult(
        contract = ScanCustomCode(),
        onResult = { qrResult ->
            when (qrResult) {
                is QRResult.QRSuccess -> {
                    component.onQrRead(qrResult.content.rawValue)
                }

                is QRResult.QRMissingPermission -> {
                    openCameraPermissionRationaleDialog = true
                }

                else -> {}
            }
        })

    Column(modifier = modifier) {

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
            onValueChanged = component::onHostChanged,
            placeholder = stringResource(R.string.server_config_host),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            isError = state.isHostError,
            errorMessage = stringResource(id = R.string.field_required)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.port,
            onValueChanged = component::onPortChanged,
            placeholder = stringResource(R.string.server_config_port),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            isError = state.isPortError,
            errorMessage = stringResource(id = R.string.field_required)
        )


        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.uuid,
            onValueChanged = component::onUUidChanged,
            placeholder = stringResource(R.string.server_config_uuid),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            isError = state.isUuidError,
            errorMessage = stringResource(id = R.string.field_required)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.other_settings),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ClickableTextField(
            value = state.networkType.name,
            placeholder = stringResource(R.string.server_config_network_type),
            modifier = Modifier
                .padding(horizontal = 16.dp),
            onClick = {
                showSelectNetworkBottomSheet = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))


        ClickableTextField(
            value = state.security?.name ?: "",
            placeholder = stringResource(R.string.server_config_security),
            modifier = Modifier
                .padding(horizontal = 16.dp),
            onClick = {
                showSelectSecurityBottomSheet = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ClickableTextField(
            value = state.flow?.name ?: "",
            placeholder = stringResource(R.string.server_config_flow),
            modifier = Modifier
                .padding(horizontal = 16.dp),
            onClick = {
                showSelectFlowBottomSheet = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.sni,
            onValueChanged = component::onSniChanged,
            placeholder = "SNI",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.publicKey ?: "",
            onValueChanged = component::onPublicKeyChanged,
            placeholder = stringResource(R.string.server_config_public_key),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.shortId ?: "",
            onValueChanged = component::onShortIdChanged,
            placeholder = stringResource(R.string.server_config_short_id),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
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

    if (showSelectNetworkBottomSheet) {
        ItemListBottomSheet(
            title = stringResource(R.string.server_config_network_type),
            items = VlessNetworkType.entries.map { it.name },
            onItemClick = {
                component.onNetworkTypeChanged(it)
                showSelectNetworkBottomSheet = false
            },
            onDismiss = { showSelectNetworkBottomSheet = false }
        ) {
            Text(
                it,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    if (showSelectSecurityBottomSheet) {
        ItemListBottomSheet(
            title = stringResource(R.string.server_config_security),
            items = VlessSecurity.entries.map { it.name },
            onItemClick = {
                component.onSecurityChanged(it)
                showSelectSecurityBottomSheet = false
            },
            onDismiss = { showSelectSecurityBottomSheet = false }
        ) {
            Text(
                it,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    if (showSelectFlowBottomSheet) {
        ItemListBottomSheet(
            title = stringResource(R.string.server_config_flow),
            items = VlessFlow.entries.map { it.name },
            onItemClick = {
                component.onFlowChanged(it)
                showSelectFlowBottomSheet = false
            },
            onDismiss = { showSelectFlowBottomSheet = false }
        ) {
            Text(
                it,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Preview(backgroundColor = 0xFFFFFFFF, showSystemUi = false, showBackground = true)
@Composable
private fun XRayServerConfigFormPreview() {
    XRayServerConfigForm(FakeXrayVpnServerConfigComponent())
}
