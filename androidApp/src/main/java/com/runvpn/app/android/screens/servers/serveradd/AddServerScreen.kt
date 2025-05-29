package com.runvpn.app.android.screens.servers.serveradd

import android.app.Activity
import android.net.VpnService
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getProtocolName
import com.runvpn.app.android.ext.openAppSettings
import com.runvpn.app.android.screens.home.checkNeedRequestNotificationsPermission
import com.runvpn.app.android.screens.servers.serveradd.openvpn.OpenVpnServerConfigForm
import com.runvpn.app.android.screens.servers.serveradd.oversocks.OverSocksServerConfigForm
import com.runvpn.app.android.screens.servers.serveradd.wireguard.WireGuardServerConfigForm
import com.runvpn.app.android.screens.servers.serveradd.xray.XRayServerConfigForm
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.ClickableTextField
import com.runvpn.app.android.widgets.CommonAlertDialog
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.testConnectionButtonContainer
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.feature.serveradd.AddServerComponent
import com.runvpn.app.feature.serveradd.FakeAddServerComponent
import com.runvpn.app.feature.serveradd.ikev2.Ikev2ConfigComponent
import com.runvpn.app.feature.serveradd.openvpn.OpenVpnServerConfigComponent
import com.runvpn.app.feature.serveradd.oversocks.OverSocksConfigComponent
import com.runvpn.app.feature.serveradd.wireguard.WireGuardConfigComponent
import com.runvpn.app.feature.serveradd.xray.XrayVpnServerConfigComponent
import com.runvpn.app.tea.dialog.DialogMessage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddServerScreen(component: AddServerComponent, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val chooseProtocolSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded })

    var showChooseProtocolBs by remember { mutableStateOf(false) }

    val state by component.state.subscribeAsState()
    val configComponent by component.configComponent.subscribeAsState()


    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    if (state.showNameError) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    var permissionGrantedAction = remember { {} }
    val vpnResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK) {
                    component.onConnectClick()
                }
            })

    var openNotificationPermissionRationaleDialog by remember {
        mutableStateOf(false)
    }

    val requestNotificationPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = { result ->
                if (result) {
                    permissionGrantedAction()
                } else {
                    openNotificationPermissionRationaleDialog = true
                }
            })

    fun onConnectToServerClick() {
        permissionGrantedAction = {
            component.onConnectClick()
        }

        val isNeedToRequestPermissions = checkNeedRequestNotificationsPermission(context)

        if (isNeedToRequestPermissions) {
            permissionGrantedAction = {
                val intent = VpnService.prepare(context)
                if (intent != null) {
                    vpnResultLauncher.launch(intent)
                } else {
                    component.onConnectClick()
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermissionLauncher.launch(
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            }
        } else {
            val intent = VpnService.prepare(context)
            if (intent != null) {
                vpnResultLauncher.launch(intent)
            } else {
                permissionGrantedAction()
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
        ) {

            IconButton(
                onClick = { component.onBackClick() }, modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.my_servers),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.my_servers_desc),
                    fontSize = 14.sp,
                    color = hintTextColor,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {

            AppTextField(
                value = state.name,
                onValueChanged = component::onNameChange,
                placeholder = stringResource(R.string.server_name),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                isError = state.showNameError,
                errorMessage = stringResource(id = R.string.error_enter_server_name),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            keyboardController?.show()
                        }
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ClickableTextField(
                value = getNullableProtocolName(state.protocol),
                placeholder = stringResource(id = R.string.choose_protocol),
                onClick = { if (!state.isInEditMode) showChooseProtocolBs = true },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { component.onIsPublicChange(!state.isPublic) }
                .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically) {

                Checkbox(
                    checked = state.isPublic,
                    onCheckedChange = component::onIsPublicChange,
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = textHintColor
                    )
                )

                Column {
                    Text(
                        text = stringResource(R.string.make_server_public),
                        lineHeight = 16.sp,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.make_server_public_desc),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = textHintColor
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(visible = configComponent.child?.instance != null) {
                configComponent.child?.instance?.let {
                    when (it) {
                        is OpenVpnServerConfigComponent -> {
                            OpenVpnServerConfigForm(it, modifier = Modifier.fillMaxWidth())
                        }

                        is XrayVpnServerConfigComponent -> {
                            XRayServerConfigForm(component = it)
                        }

                        is WireGuardConfigComponent -> {
                            WireGuardServerConfigForm(
                                component = it, modifier = Modifier.fillMaxWidth()
                            )
                        }

                        is OverSocksConfigComponent -> {
                            OverSocksServerConfigForm(component = it)
                        }

                        is Ikev2ConfigComponent -> {
                            Ikev2ServerConfigForm(component = it)
                        }
                    }

                }
            }

            if (state.showTestConnectionResult) {
                Spacer(modifier = Modifier.height(16.dp))

                TestConnectionResult(
                    connectionStatus = state.connectionStatus,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onClick = {
//                    val intent = VpnService.prepare(context)
//
//                    if (intent != null) {
//                        vpnResultLauncher.launch(intent)
//                    } else {
//                        component.onConnectClick()
//                    }
                    onConnectToServerClick()
                },
                containerColor = testConnectionButtonContainer,
                disabledContainerColor = testConnectionButtonContainer,
                contentColor = colorIconAccent,
                enabled = state.buttonAddEnabled
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_test_connection),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(text = stringResource(R.string.test_connection), fontSize = 16.sp)
            }

            AppButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                onClick = component::onServerAddClick,
                enabled = state.buttonAddEnabled
            ) {
                Text(
                    text = stringResource(
                        id = if (state.isInEditMode) R.string.save_changes else R.string.add_server
                    ), fontSize = 16.sp
                )
            }
        }
        if (openNotificationPermissionRationaleDialog) {
            CommonAlertDialog(
                onDismissRequest = { openNotificationPermissionRationaleDialog = false },
                onConfirmation = {
                    context.openAppSettings()
                    openNotificationPermissionRationaleDialog = false
                },
                dialogMessage = DialogMessage.Common(
                    title = stringResource(id = R.string.permission_notifications_rationale_title),
                    message = stringResource(id = R.string.permission_notifications_rationale_desc),
                    positiveButtonText = stringResource(id = R.string.open_settings)
                ),
                icon = Icons.Default.Info
            )
        }

        if (showChooseProtocolBs) {
            ChooseProtocolBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                sheetState = chooseProtocolSheetState,
                onDismiss = { showChooseProtocolBs = false },
                onItemClick = {
                    component.onProtocolChange(it.name)
                },
                onFaqClick = component::onFaqClick
            )
        }
    }
}

@Composable
private fun getNullableProtocolName(protocol: ConnectionProtocol?): String {
    val nonNullProtocol = protocol ?: return ""
    return stringResource(id = getProtocolName(nonNullProtocol))
}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun ServerAddScreenPreview() {
    AddServerScreen(component = FakeAddServerComponent())
}
