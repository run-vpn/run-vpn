package com.runvpn.app.android.screens.settings.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.screens.settings.main.SettingItem
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppSwitch
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.feature.settings.connection.ConnectionSettingsComponent
import com.runvpn.app.feature.settings.connection.FakeConnectionSettingsComponent

@Composable
fun ConnectionSettingsScreen(
    component: ConnectionSettingsComponent,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val state by component.state.subscribeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        AppToolBar(
            onBackClick = { dispatchOnBackPressed(context) },
            iconBack = painterResource(id = R.drawable.ic_arrow_back),
            title = stringResource(R.string.connection_settings),
            subtitle = stringResource(R.string.connection_settings_desc)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_printer),
            title = stringResource(R.string.lan),
            description = stringResource(R.string.lan_desc),
            rightView = {
                AppSwitch(
                    checked = state.allowLanConnections,
                    onCheckedChange = component::onAllowLanConnectionsToggle
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { component.onAllowLanConnectionsToggle(!state.allowLanConnections) }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_cloud_slash),
            title = stringResource(id = R.string.settings_split_tunneling),
            description = stringResource(id = R.string.settings_split_tunneling_desc),
            onClick = { component.onSplitTunnellingClick() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_nodes_tree),
            title = stringResource(id = R.string.settings_dns_title),
            description = stringResource(id = R.string.settings_dns_desc),
            onClick = { component.onDnsServersClick() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )
    }

}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, locale = "ru")
@Composable
private fun ConnectionSettingsScreenPreview() {
    ConnectionSettingsScreen(FakeConnectionSettingsComponent())
}
