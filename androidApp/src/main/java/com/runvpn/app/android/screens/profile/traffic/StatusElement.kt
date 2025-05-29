package com.runvpn.app.android.screens.profile.traffic

//import com.android.asdk.wrapper.app.logs.model.StatusType
//import com.android.asdk.wrapper.app.model.Statistics
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.toConnTimerFormat
import com.runvpn.app.android.screens.profile.traffic.utils.rememberStopwatchTimer
import com.runvpn.app.core.ui.bgConnectionError
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.settingsValueTextColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textGrayColor
import com.runvpn.app.core.ui.tfsConnectedBackground
import com.runvpn.app.core.ui.tfsConnectedText
import com.runvpn.app.core.ui.tfsConnectingBackground
import com.runvpn.app.core.ui.tfsConnectingText
import com.runvpn.app.core.ui.underLineColor
import com.runvpn.app.data.device.data.models.traffic.TrafficModuleInfo
import com.runvpn.app.feature.trafficmodule.FakeTrafficModuleComponent
import com.runvpn.app.feature.trafficmodule.TrafficModuleComponent

@Composable
fun Status(trafficModuleInfo: TrafficModuleInfo, component: TrafficModuleComponent) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        val tabs = listOf(
            stringResource(id = R.string.tfs_device_curr),
            stringResource(id = R.string.tfs_device_all)
        )

        TabRow(selectedTabIndex = tabIndex,
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[tabIndex])
                        .height(2.dp)
                        .padding(horizontal = 0.dp)
                        .background(color = underLineColor)
                )
            }) {
            tabs.forEachIndexed { index, title ->
                CustomTab(text = {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (tabIndex == index) Color.Black else settingsValueTextColor,
                    )
                }, onClick = { tabIndex = index })
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(width = 2.dp, color = lightBlue, shape = RoundedCornerShape(12.dp))
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.tfs_status),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = textGrayColor
                )
            }

//            val (currState, logText) = rememberProxyStatus(component)

            val currDevice = (tabIndex == 0)

//            when (currState.type) {
//                StatusType.CONNECTED ->
//                    ShowStatusConnected(currDevice, component, trafficModuleInfo)
//
//                StatusType.ERROR ->
//                    ShowStatusError("${currState.status}:${currState.host}:${currState.desc}")
//
//                else -> ShowStatusConnecting(logText)
//            }

            if (currDevice) ShowStatSdk(component)
        }
    }
}

// region Status


@Composable
fun CustomTab(
    onClick: () -> Unit,
    text: @Composable (() -> Unit)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 0.dp, vertical = 8.dp)
            .clickable { onClick() },

        ) {
        text()
    }
}


@Composable
fun ShowStatSdk(component: TrafficModuleComponent) {

    rememberStopwatchTimer()

//    val stat = component.getStatistics() as Statistics

    TrafficLabel(
        R.drawable.ic_traff_give,
        R.string.tfs_sdk_total_traff,
        ""//stat.totalTrafficBytes.toHumanReadableByteCountSI
    )
    TrafficLabel(
        R.drawable.ic_traff_online,
        R.string.tfs_sdk_online,
        ""
//        stat.totalTimeOnlineMs.toConnTimerFormat(
//            stringResource(id = R.string.tfs_counter_format)
//        )
    )
    TrafficLabel(
        R.drawable.ic_traff_reconnect,
        R.string.tfs_traff_reconnect,
        ""
//        stat.totalConnectErrorsCount.toString()
    )
    val sessionTime = 0L
//        if (stat.totalConnectCount == 0L) 0L else (stat.totalTimeOnlineMs / stat.totalConnectCount)
    TrafficLabel(
        R.drawable.ic_traff_session,
        R.string.tfs_traff_session,
        sessionTime.toConnTimerFormat(stringResource(id = R.string.tfs_counter_format)),
        true
    )
}

@Composable
fun ShowStatusError(msg: String) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .background(bgConnectionError, RoundedCornerShape(8.dp))
    ) {

        Spacer(modifier = Modifier.width(16.dp))

        Box(contentAlignment = Alignment.CenterStart) {
            Image(
                painter = painterResource(id = R.drawable.ic_connection_error),
                contentDescription = null
            )

            Text(
                text = stringResource(R.string.tfs_status_error),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = textErrorColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
    }

    Text(text = msg, fontSize = 12.sp, color = textErrorColor, fontWeight = FontWeight.Bold)

    Text(
        text = stringResource(R.string.tfs_status_error_connecting),
        fontSize = 12.sp,
        color = textErrorColor
    )

    Image(
        painter = painterResource(id = R.drawable.ic_traff_proxy_error),
        contentDescription = null,
        modifier = Modifier
            .width(98.dp)
            .height(56.dp)
            .padding(bottom = 16.dp)
    )
}

@Composable
fun ShowStatusConnected(
    currDevice: Boolean,
    component: TrafficModuleComponent, trafficModuleInfo: TrafficModuleInfo
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 4.dp)
            .background(tfsConnectedBackground, RoundedCornerShape(8.dp))
    ) {

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_circle_done),
            contentDescription = null,
            tint = greenColor
        )

        Text(
            text = stringResource(R.string.tfs_status_connected),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = tfsConnectedText,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))
    }

    if (currDevice) {
        StatisticElementCurrDevice(component)
    } else {
        val outputTraffic = trafficModuleInfo.allDevTotal.accumBytes
        val inputTraffic = trafficModuleInfo.allDevTotal.usedBytes
        StatisticElementAllDevices(outputTraffic, inputTraffic)
    }
}

@Composable
fun ShowStatusConnecting(logText: AnnotatedString) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 4.dp)
            .background(tfsConnectingBackground, RoundedCornerShape(8.dp))
    ) {

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(R.string.tfs_status_connecting),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = tfsConnectingText,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )

        CircularProgressIndicator(
            color = tfsConnectingBackground,
            trackColor = tfsConnectingText,
            strokeWidth = 2.dp,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))
    }

    Log(logText)

    Image(
        painter = painterResource(id = R.drawable.ic_traff_connecting),
        contentDescription = null,
        modifier = Modifier
            .width(144.dp)
            .height(40.dp)
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun Log(logText: AnnotatedString) {
    val logScroll = rememberScrollState()
    LaunchedEffect(logText) {
        logScroll.scrollTo(logScroll.maxValue)
    }
    Row(modifier = Modifier.height(52.dp)) {
        Text(
            text = logText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 43.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(logScroll)
        )
    }
}

//internal fun getLogTextColor(type: StatusType): Color {
//    if (type == StatusType.CONNECTED) return tfsConnectedText
//    if (type == StatusType.ERROR) return textErrorColor
//    return tfsConnectingText
//}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun StatusPreview() {
    Status(TrafficModuleInfo(), FakeTrafficModuleComponent())
}
// endregion Status
