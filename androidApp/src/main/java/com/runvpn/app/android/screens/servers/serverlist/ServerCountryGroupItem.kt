package com.runvpn.app.android.screens.servers.serverlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.getIconResId
import com.runvpn.app.android.utils.WidgetPreview
import com.runvpn.app.android.widgets.BorderedContainerView
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.lightBlue2
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textAccentColor
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.utils.TestData


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServerCountryGroupItem(
    serverGroup: Pair<String?, List<Server>>,
    modifier: Modifier = Modifier,
    onFavouriteClick: (Server, Boolean) -> Unit,
    onClick: (Server) -> Unit
) {
//    val isPreview = LocalInspectionMode.current

    var cityVisible by rememberSaveable { mutableStateOf(false) }

    val serverCountLabelBackground = remember(cityVisible) {
        if (cityVisible) {
            lightBlue
        } else {
            if (serverGroup.second.size > 10) {
                primaryColor
            } else {
                lightBlue
            }
        }
    }

    BorderedContainerView(
        strokeWidth = if (cityVisible) {
            1.dp
        } else {
            0.dp
        },
        containerColor = if (cityVisible) lightBlue2 else Color.Transparent,
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { cityVisible = !cityVisible }
            ) {
                Image(
                    painter = painterResource(
                        serverGroup.second.first().getIconResId(LocalContext.current)
                    ),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .padding(12.dp)
                        .width(40.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(2.dp))
                )

                Column {
                    Text(
                        text = serverGroup.first
                            ?: stringResource(id = R.string.unsorted_server_group),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = pluralStringResource(
                        id = R.plurals.server_format,
                        count = serverGroup.second.size,
                        serverGroup.second.size
                    ),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = if (serverGroup.second.size > 10 && !cityVisible) Color.White else textAccentColor,
                    modifier = Modifier
                        .background(serverCountLabelBackground, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_server_group_toggle),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .rotate(if (cityVisible) 0f else 180f),
                    tint = Color.Unspecified

                )
            }

            AnimatedVisibility(visible = cityVisible) {
                FlowColumn(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                ) {
                    HorizontalDivider(color = lightBlue)

                    serverGroup.second.forEach { server ->
                        ServerItem(
                            server = server,
                            onClick = onClick,
                            modifier = Modifier.fillMaxWidth(),
                            onFavouriteClick = onFavouriteClick
                        )
                    }
                }
            }
        }
    }
}

@WidgetPreview
@Composable
private fun ServerCountryGroupItemPreview() {
    RunVpnTheme {
        Box(
            modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
        ) {
            ServerCountryGroupItem(
                serverGroup = "Russia" to TestData.testServerList,
                onFavouriteClick = { _, _ -> }
            ) {

            }
        }
    }
}

@WidgetPreview
@Composable
private fun ServerCountryGroupItemPreview2() {
    val servers = TestData.testServerList.toMutableList().apply {
        repeat(2) {
            addAll(TestData.testServerList)
        }
    }
    RunVpnTheme {
        Box(modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)) {
            ServerCountryGroupItem(
                serverGroup = "Russia" to servers,
                onFavouriteClick = { _, _ -> }
            ) {}
        }
    }
}
