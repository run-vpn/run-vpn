package com.runvpn.app.android.screens.settings.common

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.toLocaleString
import com.runvpn.app.android.screens.settings.main.SettingItem
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppSwitch
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.core.common.presentation.Notification
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.core.ui.settingsValueTextColor
import com.runvpn.app.feature.settings.common.CommonSettingsComponent
import com.runvpn.app.feature.settings.common.FakeCommonSettingsComponent
import com.runvpn.app.feature.settings.suggestedserversmode.ChooseSuggestedServersModeComponent

@Composable
fun CommonSettingsScreen(component: CommonSettingsComponent, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val state by component.state.subscribeAsState()

    val dialogChild by component.childSlot.subscribeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        AppToolBar(
            onBackClick = { dispatchOnBackPressed(context) },
            iconBack = painterResource(id = R.drawable.ic_arrow_back),
            title = stringResource(id = R.string.settings_common_settings),
            subtitle = stringResource(id = R.string.settings_common_settings_desc)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_circle_done),
            title = stringResource(id = R.string.startup),
            description = stringResource(id = R.string.startup_desc),
            rightView = {
                AppSwitch(
                    checked = state.runAfterDeviceStart,
                    onCheckedChange = component::onStartupToggled
                )
            },
            onClick = { component.onStartupToggled(!state.runAfterDeviceStart) },
            modifier = Modifier
                .fillMaxWidth()
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SettingItem(
                iconPainter = painterResource(id = R.drawable.ic_notification),
                title = stringResource(id = R.string.setting_hide_disconnect_notification),
                description = null,
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        putExtra(
                            Settings.EXTRA_CHANNEL_ID,
                            Notification.ChannelIdDisconnected
                        )
                    }
                    context.startActivity(intent)
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = colorStrokeSeparator
            )
        }

        SettingItem(iconPainter = painterResource(id = R.drawable.ic_location),
            title = stringResource(R.string.suggested_servers_mode),
            rightView = {
                Text(
                    text = state.suggestedServersMode.toLocaleString(context),
                    color = settingsValueTextColor,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            },
            onClick = { component.onChangeSuggestedServersModeClicked() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SettingItem(
                iconPainter = painterResource(id = R.drawable.ic_location_2),
                title = stringResource(R.string.settings_vpn_kill_switch),
                description = stringResource(R.string.settings_kill_switch_desc),
                onClick = {
                    val intent = Intent(Settings.ACTION_VPN_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = colorStrokeSeparator
            )
        }

        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_device),
            title = stringResource(R.string.auto_connect),
            description = stringResource(R.string.auto_connect_desc),
            rightView = {
                AppSwitch(
                    checked = state.autoConnect,
                    onCheckedChange = component::onAutoConnectToggled
                )
            },
            onClick = { component.onAutoConnectToggled(!state.autoConnect) }
        )

        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_signal),
            title = stringResource(R.string.auto_reconnect),
            description = stringResource(R.string.auto_reconnect_desc),
            rightView = {
                AppSwitch(
                    checked = state.reconnectToNextServer,
                    onCheckedChange = component::onReconnectToNextServerToggled
                )
            },
            onClick = { component.onReconnectToNextServerToggled(!state.reconnectToNextServer) },
            modifier = Modifier
                .fillMaxWidth()

        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_power_outline),
            title = stringResource(R.string.auto_disconnect),
            description = stringResource(R.string.auto_disconnect_desc),
            rightView = {
                AppSwitch(
                    checked = state.autoDisconnect,
                    onCheckedChange = component::onAutoDisconnectToggled
                )
            },
            onClick = { component.onAutoDisconnectToggled(!state.autoDisconnect) },
            modifier = Modifier
                .fillMaxWidth()
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_signal_2),
            title = stringResource(R.string.connect_on_network_enabled),
            description = stringResource(R.string.connect_on_network_enabled_desc),
            rightView = {
                AppSwitch(
                    checked = state.connectOnNetworkEnabled,
                    onCheckedChange = component::onConnectOnNetworkEnabledToggled
                )
            },
            onClick = { component.onConnectOnNetworkEnabledToggled(!state.connectOnNetworkEnabled) },
            modifier = Modifier
                .fillMaxWidth()
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_hand),
            title = stringResource(R.string.tactile_feedback),
            description = stringResource(R.string.tactile_feedback_desc),
            rightView = {
                AppSwitch(
                    checked = state.tactileFeedback,
                    onCheckedChange = component::onTactileFeedbackToggled
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { component.onTactileFeedbackToggled(!state.tactileFeedback) }
        )

    }

    when (val child = dialogChild.child?.instance) {
        is ChooseSuggestedServersModeComponent -> ChooseSuggestedServersModeBs(child)
    }

}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, locale = "ru")
@Composable
private fun CommonSettingsScreenPreview() {
    CommonSettingsScreen(FakeCommonSettingsComponent())
}
