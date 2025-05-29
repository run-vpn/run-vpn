package com.runvpn.app.android.screens.settings.main

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.installUpdate
import com.runvpn.app.android.utils.ScreenPreview
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.core.ui.settingsValueTextColor
import com.runvpn.app.core.ui.textPrimaryColor
import com.runvpn.app.feature.settings.language.ChooseLanguageComponent
import com.runvpn.app.feature.settings.main.FakeSettingsComponent
import com.runvpn.app.feature.settings.main.MainSettingsComponent


@Composable
fun MainSettingsScreen(component: MainSettingsComponent, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val state by component.state.subscribeAsState()
    val dialogChild by component.childSlot.subscribeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.tab_settings),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = textPrimaryColor,
            modifier = Modifier.padding(16.dp)
        )

        state.updateStatus?.let { status ->
            state.updateProgress?.let { progress ->
                UpdateAvailableView(
                    updateStatus = status,
                    updateProgress = progress,
                    onClick = { fileName ->
                        context.installUpdate(fileName)
                    },
                    onRetryClick = component::onRetryDownloadClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        SettingItem(
            title = stringResource(id = R.string.language),
            rightView = {
                Text(
                    text = state.language?.language ?: "",
                    color = settingsValueTextColor,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            },
            iconPainter = painterResource(id = R.drawable.ic_settings_language),
            onClick = component::onChooseLanguageClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        SettingItem(
            title = stringResource(id = R.string.settings_common_settings),
            description = stringResource(id = R.string.settings_common_settings_desc),
            iconPainter = painterResource(id = R.drawable.ic_settings_computer),
            onClick = component::onCommonSettingsClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        SettingItem(
            title = stringResource(id = R.string.connection_settings),
            description = stringResource(id = R.string.connection_settings_desc),
            iconPainter = painterResource(id = R.drawable.ic_split_tunneling),
            onClick = component::onConnectionSettingsClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        SettingItem(
            title = stringResource(id = R.string.support),
            description = stringResource(id = R.string.support_desc),
            iconPainter = painterResource(id = R.drawable.ic_support_chat),
            onClick = component::onSupportClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        SettingItem(
            title = stringResource(id = R.string.about_application),
            description = stringResource(id = R.string.sup_info_desc),
            iconPainter = painterResource(id = R.drawable.ic_settings_support),
            onClick = component::onAboutClick
        )

    }

    when (val child = dialogChild.child?.instance) {
        is ChooseLanguageComponent -> ChooseLanguageBs(child)
    }
}

@ScreenPreview
@Composable
fun MainSettingScreenPreview() {
    RunVpnTheme {
        MainSettingsScreen(
            component = FakeSettingsComponent(
                isUpdateAvailable = false
            )
        )
    }
}
