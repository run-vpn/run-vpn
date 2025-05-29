package com.runvpn.app.android.screens.settings.connection.splittunneling

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.screens.settings.main.SettingItem
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.feature.settings.splittunneling.main.FakeSplitTunnelingMainComponent
import com.runvpn.app.feature.settings.splittunneling.main.SplitTunnelingMainComponent

@Composable
fun SplitTunnelingMainScreen(
    component: SplitTunnelingMainComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    val ipsString by remember {
        mutableStateOf(state.ips.joinToString(", "))
    }
    val appsString by remember {
        mutableStateOf(state.apps.joinToString(", ") { it.name })
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        AppToolBar(
            onBackClick = component::onBackClick,
            title = stringResource(id = R.string.settings_split_tunneling),
            subtitle = stringResource(id = R.string.settings_split_tunneling_desc)
        )

        SettingItem(
            iconPainter = painterResource(id = R.drawable.unblock),
            title = stringResource(R.string.excluded_ip_addresses),
            description = if (state.ips.isEmpty()) stringResource(id = R.string.none) else ipsString,
            descriptionMaxLines = 2,
            onClick = { component.onSplitIpsClick() }
        )

        Divider(color = colorStrokeSeparator, modifier = Modifier.padding(horizontal = 16.dp))

        SettingItem(
            iconPainter = painterResource(id = R.drawable.unblock),
            title = stringResource(R.string.excluded_applications),
            description = if (state.apps.isEmpty()) stringResource(id = R.string.none) else appsString,
            descriptionMaxLines = 2,
            onClick = { component.onSplitAppsClick() }
        )

        Divider(color = colorStrokeSeparator, modifier = Modifier.padding(horizontal = 16.dp))
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SplitTunnelingMainScreenPreview() {
    SplitTunnelingMainScreen(component = FakeSplitTunnelingMainComponent())
}

