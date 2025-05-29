package com.runvpn.app.android.screens.profile.traffic

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.tabIndicatorOffsetMy
import com.runvpn.app.android.ext.toConnTimerFormat
import com.runvpn.app.android.ext.toHumanReadableByteCountSI
import com.runvpn.app.android.ext.toMinutes
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textGrayColor
import com.runvpn.app.core.ui.tfsDivider
import com.runvpn.app.core.ui.tfsStatTabBackground
import com.runvpn.app.data.device.data.models.traffic.TrafficModuleInfo
import com.runvpn.app.feature.trafficmodule.FakeTrafficModuleComponent
import com.runvpn.app.feature.trafficmodule.TrafficModuleComponent

@Composable
fun TrafficModuleScreen(component: TrafficModuleComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()
    val trafficInfo = state.trafficModuleInfo

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        AppToolBar(
            onBackClick = { component.onBack() },
            title = stringResource(id = R.string.tfs_title),
            subtitle = stringResource(
                id = R.string.tfs_desc
            )
        )

        Status(trafficInfo, component)

        Row {
            AppButton(
                onClick = { component.onLookLogs() },
                containerColor = Color.Transparent,
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.tfs_look_logs),
                    color = primaryColor,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        StatisticsAccum(trafficInfo)
    }
}

// region Statistics
@Composable
fun TrafficLabel(iconId: Int, text: Int, value: String, last: Boolean = false) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.padding(end = 4.dp)
        )

        Text(
            text = stringResource(id = text),
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            color = textGrayColor
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            textAlign = TextAlign.Right,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }

    if (!last) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp, color = tfsDivider
        )
    }
}

@Composable
fun StatisticsAccum(trafficModuleInfo: TrafficModuleInfo) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.tfs_stat_title),
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val tabs = listOf(
            stringResource(id = R.string.tfs_stat_all),
            stringResource(id = R.string.tfs_stat_today),
            stringResource(id = R.string.tfs_stat_yesterday)
        )

        TabRow(selectedTabIndex = tabIndex,
            containerColor = tfsStatTabBackground,
            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
            divider = {},
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffsetMy(tabPositions[tabIndex])
                        .fillMaxSize()
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .shadow(elevation = 5.dp, spotColor = Color.Transparent)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    Tab(selectedContentColor = tfsStatTabBackground, text = {
                        Text(
                            text = tabs[tabIndex],
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }, selected = true, onClick = { })
                }
            }) {
            tabs.forEachIndexed { index, title ->
                Tab(selectedContentColor = tfsStatTabBackground, text = {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                }, selected = tabIndex == index, onClick = { tabIndex = index })
            }
        }
    }


    val (trafficInfo, onlineTime) = when (tabIndex) {
        1 -> Pair(
            trafficModuleInfo.currDevToday, stringResource(
                id = R.string.tfs_traff_online_time,
                trafficModuleInfo.currDevToday.onlineTimeMs.toMinutes
            )
        )

        2 -> Pair(
            trafficModuleInfo.currDevYesterday, stringResource(
                id = R.string.tfs_traff_online_time,
                trafficModuleInfo.currDevYesterday.onlineTimeMs.toMinutes
            )
        )

        else -> Pair(
            trafficModuleInfo.currDevTotal,
            trafficModuleInfo.currDevTotal.onlineTimeMs.toConnTimerFormat(
                stringResource(id = R.string.tfs_counter_format)
            )
        )
    }

    TrafficLabel(R.drawable.ic_traff_used, R.string.tfs_traff_used, "0")
    TrafficLabel(
        R.drawable.ic_traff_give,
        R.string.tfs_traff_give,
        trafficInfo.accumBytes.toHumanReadableByteCountSI
    )

    TrafficLabel(
        R.drawable.ic_traff_online, R.string.tfs_traff_online, onlineTime
    )
    TrafficLabel(
        R.drawable.ic_traff_reconnect,
        R.string.tfs_traff_reconnect,
        trafficInfo.reconnectCount.toString()
    )
    TrafficLabel(
        R.drawable.ic_traff_session,
        R.string.tfs_traff_session,
        (trafficInfo.onlineTimeMs / (trafficInfo.reconnectCount + 1)).toConnTimerFormat(
            stringResource(id = R.string.tfs_counter_format)
        ),
        true
    )
}
// endregion Statistics

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TrafficModuleScreenPreview() {
    RunVpnTheme {
        TrafficModuleScreen(FakeTrafficModuleComponent())
    }
}
