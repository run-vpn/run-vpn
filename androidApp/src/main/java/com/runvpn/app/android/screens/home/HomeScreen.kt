package com.runvpn.app.android.screens.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.VpnService
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.installUpdate
import com.runvpn.app.android.ext.openAppSettings
import com.runvpn.app.android.monacoFontFamily
import com.runvpn.app.android.screens.home.sharedialog.ShareQrCodeBottomSheet
import com.runvpn.app.android.screens.home.sharedialog.ShareWithFriendsBottomSheet
import com.runvpn.app.android.widgets.ButtonArrowRight
import com.runvpn.app.android.widgets.CommonAlertDialog
import com.runvpn.app.android.widgets.ElevatedContainerView
import com.runvpn.app.android.widgets.HorizontalStrokeDivider
import com.runvpn.app.android.widgets.LoadingShimmerView
import com.runvpn.app.core.ui.backgroundAccentColor
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.mapAccentColor
import com.runvpn.app.core.ui.mapDefaultColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textSecondaryColor
import com.runvpn.app.core.ui.ultraLightGray
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.feature.common.dialogs.connectionerror.ConnectionErrorComponent
import com.runvpn.app.feature.common.dialogs.shareqrcode.ShareQrCodeComponent
import com.runvpn.app.feature.home.FakeHomeComponent
import com.runvpn.app.feature.home.HomeComponent
import com.runvpn.app.feature.home.rating.ConnectionReviewComponent
import com.runvpn.app.feature.home.sharewithfriends.ShareWithFriendsComponent
import com.runvpn.app.tea.dialog.DialogMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(component: HomeComponent, modifier: Modifier = Modifier) {

    val state by component.state.subscribeAsState()

    val isLoading = remember(state.isLoading) { state.isLoading }
    val vpnStatus = remember(state.vpnStatus) { state.vpnStatus }
    val connectionStats = remember(state.connectionStats) { state.connectionStats }
    val vpnStatusHistory = remember(state.vpnStatusHistory) { state.vpnStatusHistory }
    val suggestedServers = remember(state.suggestedServers) { state.suggestedServers }
    val currentServer = remember(state.currentServer) { state.currentServer }
    val connectionTime = remember(state.connectionTime) { state.connectionTime }

    val reviewDialogSlot by component.reviewDialog.subscribeAsState()

    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val suggestedListState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0,
        initialFirstVisibleItemScrollOffset = 0
    )

    var permissionGrantedAction = remember { {} }

    val vpnResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK) {
                    component.onPermissionsGranted()
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

    fun onConnectToServerClick(server: Server) {
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

    // This is for list always be at first item position
    var lastSuggestedListSize = 0
    LaunchedEffect(key1 = state.suggestedServers) {
        if (lastSuggestedListSize != (state.suggestedServers?.servers?.size ?: 0)) {
            suggestedListState.scrollToItem(0, 0)
            lastSuggestedListSize = state.suggestedServers?.servers?.size ?: 0
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundAccentColor)
    ) {
        Image(
            painter = painterResource(R.drawable.im_map),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(
                when (state.vpnStatus) {
                    is ConnectionStatus.Connected -> mapAccentColor
                    else -> mapDefaultColor
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp)
        )

        /** Mark on Map*/
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 180.dp, end = 20.dp)
                .align(Alignment.TopEnd)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.bg_mark_on_map),
                    contentDescription = null
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.mark_on_map_you),
                        fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Icon(
                        painter =
                        painterResource(
                            id = if (state.vpnStatus is ConnectionStatus.Connected) {
                                R.drawable.ic_lock_locked
                            } else {
                                R.drawable.ic_lock_unlocked
                            }
                        ),

                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Icon(
                painter = painterResource(id = R.drawable.mark_on_map),
                contentDescription = null,
                tint = if (state.vpnStatus is ConnectionStatus.Connected) {
                    Color.Unspecified
                } else {
                    textHintColor
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            /** Work on Offline uncomment to enable*/
           /* if (!state.isDomainReachable && state.vpnStatus.isDisconnected()) {
                ApplicationInOfflineModeElement(
                    modifier = Modifier.padding(16.dp),
                    component::offlineElementClicked
                )
            }*/

            Spacer(modifier = Modifier.height(16.dp))

            // Top widget with traffic and network statistics
            ElevatedContainerView(
                radius = 16.dp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {

                AnimatedVisibility(
                    visible = vpnStatus is ConnectionStatus.Connected &&
                            connectionStats != null
                ) {
                    if (connectionStats != null && currentServer != null) {
                        CurrentSessionNetworkStatistics(
                            stats = state.connectionStats!!,
                            ping = state.currentPing,
                            currentServer = currentServer,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }

            AnimatedVisibility(visible = state.vpnStatus is ConnectionStatus.Connecting) {
                ConnectingLogView(vpnStatusHistory)
            }

            Spacer(modifier = Modifier.weight(1f))

            if (state.vpnStatus is ConnectionStatus.Error) {
                Text(
                    text = stringResource(id = R.string.vpn_status_error),
                    fontSize = 32.sp,
                    color = textErrorColor,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = (state.vpnStatus as ConnectionStatus.Error).message
                        ?: stringResource(id = R.string.common_error),
                    fontSize = 14.sp,
                    color = hintTextColor,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isLoading) {
                ToggleVpnView(
                    vpnStatus = vpnStatus,
                    connectionTime = connectionTime,
                    timeoutMillis = VpnConnectionManager.CONNECTION_TIMEOUT_MILLIS,
                    errorTime = state.connectionErrorTime,
                    onConnectClick = {
                        permissionGrantedAction = {
                            component.onConnectClick()
                        }

                        val isNeedToRequestPermissions =
                            checkNeedRequestNotificationsPermission(context)

                        Logger.d("Need to allow permissions")

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
                                    Manifest.permission.POST_NOTIFICATIONS
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
                    },
                    onDisconnectClick = component::onDisconnectClick
                )
            }

            ConnectToOtherServerHint(state.connectionErrorTime)

            if (currentServer != null) {
                CurrentServerItem(
                    server = currentServer,
                    onServerClicked = {
                        component.onCurrentServerClick()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
//                        ConnectedPingView(ping = state.currentPing)
                    Image(
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .size(24.dp)
                    )
                }
            } else {
                LoadingShimmerView(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(60.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.updateStatus != null && state.updateStatus is UpdateStatus.Success) {
                AppUpdateBar(
                    title = stringResource(R.string.update_downloaded),
                    newVersion = stringResource(
                        R.string.new_version,
                        (state.updateStatus as UpdateStatus.Success).updateInfo.versionName
                    ),
                    currentVersion = stringResource(
                        R.string.your_version,
                        state.appVersion?.versionName.toString()
                    ),
                    onClick = { context.installUpdate((state.updateStatus as UpdateStatus.Success).filePath) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                ServersInfoView(
                    isLoading = isLoading,
                    suggestedServers = suggestedServers,
                    onSuggestedServerClick = ::onConnectToServerClick
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
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

    when (val dialogComponent = reviewDialogSlot.child?.instance) {
        is ConnectionReviewComponent -> {
            ConnectionReviewBs(component = dialogComponent)
        }

        is ShareWithFriendsComponent -> {
            ShareWithFriendsBottomSheet(
                component = dialogComponent,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        is ShareQrCodeComponent -> {
            ShareQrCodeBottomSheet(component = dialogComponent)
        }

        is ConnectionErrorComponent -> {
            ConnectionErrorBottomSheet(component = dialogComponent)
        }
    }
}


@Composable
fun TrafficView(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .border(2.dp, greenColor, CircleShape)
                .padding(4.dp)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_network_quality),
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "43.5Gb traffic left",
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Plan: free",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight(500),
                color = textSecondaryColor
            )
        }

        ButtonArrowRight(onClick = { /*TODO*/ }, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun ConnectingLogView(connectingLogs: List<String>, modifier: Modifier = Modifier) {

    ElevatedContainerView(
        elevation = 8.dp,
        radius = 16.dp,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                fontFamily = monacoFontFamily,
                text = stringResource(id = R.string.connecting),
                color = textHintColor,
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalStrokeDivider(thickness = 1.dp, color = ultraLightGray)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(connectingLogs) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .fillMaxWidth()
                    ) {

                        Text(
                            text = it,
                            color = if (it != connectingLogs.last()) greenColor else colorIconAccent,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                            fontFamily = monacoFontFamily
                        )

                        Text(
                            text = stringResource(
                                id = if (it != connectingLogs.last())
                                    R.string.complete
                                else R.string.connecting
                            ).lowercase(),
                            color = if (it != connectingLogs.last()) greenColor
                            else colorIconAccent,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = monacoFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectToOtherServerHint(connectionErrorTime: Long, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        if (connectionErrorTime > 0L) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.retrying_to_connect_to_other_server),
                fontSize = 12.sp,
                color = hintTextColor,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}


fun checkNeedRequestNotificationsPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_DENIED
    }

    return false
}

@Preview(
    backgroundColor = 0xFFEFF2F5,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    device = "spec:width=720px,height=1250px,dpi=320",
    showBackground = true, locale = "ru-rRU"
)
@Composable
fun RootHomeScreen() {
    HomeScreen(component = FakeHomeComponent(isConnected = false))
}

