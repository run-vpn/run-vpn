package com.runvpn.app.android.screens.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textGrayColor
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.feature.settings.support.about.AboutComponent
import com.runvpn.app.feature.settings.support.about.FakeAboutComponent

@Composable
fun AboutScreen(component: AboutComponent, modifier: Modifier = Modifier) {

    val state by component.state.subscribeAsState()
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier.fillMaxSize()) {
        AppToolBar(
            onBackClick = component::onBack,
            iconBack = painterResource(id = R.drawable.ic_arrow_back),
            title = stringResource(R.string.about_application),
            subtitle = stringResource(R.string.actual_info)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row {
                Text(
                    text = stringResource(R.string.version),
                    color = textGrayColor,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = state.aboutInfo?.versionName
                        ?: stringResource(id = R.string.common_error),
                    fontSize = 16.sp
                )
            }
            Row {
                Text(
                    text = stringResource(R.string.build_number),
                    color = textGrayColor,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = state.aboutInfo?.versionCode.toString(), fontSize = 16.sp)
            }
            Row {
                Text(
                    text = stringResource(R.string.developer),
                    color = textGrayColor,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = state.ourCompany, fontSize = 16.sp)
            }

            state.deviceUuid?.let { appUuid ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(textInputBackgroundColor, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { component.onDeviceUuidClick(appUuid) }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = appUuid,
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_copy_clipboard),
                        contentDescription = ""
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { uriHandler.openUri(state.siteUrl) }
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.link_to_product_site),
                color = textGrayColor,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Text(text = state.siteUrl, color = primaryColor, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AboutScreenPreview() {
    RunVpnTheme {
        AboutScreen(component = FakeAboutComponent())
    }
}
