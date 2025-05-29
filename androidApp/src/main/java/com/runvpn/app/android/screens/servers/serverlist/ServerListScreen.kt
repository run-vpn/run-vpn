package com.runvpn.app.android.screens.servers.serverlist

import android.app.Activity
import android.content.Context
import android.net.VpnService
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.runvpn.app.android.screens.home.checkNeedRequestNotificationsPermission
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.CommonAlertDialog
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.feature.serverlist.FakeServerListComponent
import com.runvpn.app.feature.serverlist.ServerListComponent
import com.runvpn.app.tea.dialog.DialogMessage


@Composable
fun ServerListScreen(
    component: ServerListComponent, modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()

    val context = LocalContext.current

    // region Permissions
    var permissionGrantedAction = remember { {} }

    val vpnResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK) {
                    component.onPermissionGranted()
                    permissionGrantedAction()
                }
            })

    var openNotificationPermissionRationaleDialog by remember {
        mutableStateOf(false)
    }

    val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { result ->
            if (result) {
                permissionGrantedAction()
            } else {
                openNotificationPermissionRationaleDialog = true
            }
        }
    )

    var isFilterDialogShowing by remember {
        mutableStateOf(false)
    }

    fun onConnectClick(server: Server) {
        permissionGrantedAction = {
            component.onServerClicked(server)
        }

        val isNeedToRequestPermissions =
            checkNeedRequestNotificationsPermission(context)

        if (isNeedToRequestPermissions) {
            permissionGrantedAction = {
                val intent = VpnService.prepare(context)
                if (intent != null) {
                    vpnResultLauncher.launch(intent)
                } else {
                    component.onServerClicked(server)
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
    Box {

        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ServerListToolbar(onMyServersClick = component::onAddVpnServerClicked)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF6F6F6), shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isFilterDialogShowing = true }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = state.filter.toLocalizedString(context) +
                            " (${state.customServers.size + state.remoteServers.size})",
                    color = Color(0xFF2C2D2E),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_bottom),
                    contentDescription = "",
                    tint = Color(0xFF919399)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (state.remoteServers.isEmpty() && state.customServers.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_no_content),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = stringResource(R.string.no_added_servers),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.add_custom_servers_hint),
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn {
                    items(state.remoteServers, key = { it.first ?: "null" }) { server ->
                        ServerCountryGroupItem(
                            modifier = Modifier.fillMaxWidth(),
                            serverGroup = server,
                            onFavouriteClick = component::onSetServerFavouriteClicked,
                            onClick = ::onConnectClick
                        )

                        HorizontalDivider(
                            color = Color(0xFFEBEBEB),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    items(state.customServers, key = { it.uuid }) {
                        SingleServerItem(
                            modifier = Modifier.fillMaxWidth(),
                            server = it,
                            onClick = ::onConnectClick,
                            onFavouriteClick = component::onSetServerFavouriteClicked
                        )
                        HorizontalDivider(
                            color = Color(0xFFEBEBEB),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        AppButton(
            onClick = component::onAddVpnServerClicked,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_tint),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = R.string.my_servers),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }

    if (isFilterDialogShowing) {
        ServerListFilterBs(
            onFilterSelected = {
                component.onFilterChanged(it)
                isFilterDialogShowing = false
            },
            onDismiss = { isFilterDialogShowing = false }
        )
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
}

fun ServerListComponent.ServerListFilter.toLocalizedString(
    context: Context
): String = when (this) {
    ServerListComponent.ServerListFilter.ALL -> context.getString(R.string.server_filter_all)
    ServerListComponent.ServerListFilter.RUN_SERVICE -> context.getString(R.string.server_filter_run_service)
    ServerListComponent.ServerListFilter.RUN_CLIENT -> context.getString(R.string.server_filter_run_client)
    ServerListComponent.ServerListFilter.CUSTOM -> context.getString(R.string.server_filter_custom)
}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, locale = "ru")
@Composable
fun PreviewServerListPage() {
    ServerListScreen(component = FakeServerListComponent())
}
