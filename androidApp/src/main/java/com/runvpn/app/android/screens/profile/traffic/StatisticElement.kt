package com.runvpn.app.android.screens.profile.traffic

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.android.asdk.wrapper.app.model.Statistics
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.toConnTimerFormat
import com.runvpn.app.android.ext.toHumanReadableByteCountSI
import com.runvpn.app.android.screens.profile.traffic.utils.CountersModel
import com.runvpn.app.android.screens.profile.traffic.utils.rememberStopwatchTimer
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.tfsDivider
import com.runvpn.app.core.ui.tfsInputText
import com.runvpn.app.feature.trafficmodule.FakeTrafficModuleComponent
import com.runvpn.app.feature.trafficmodule.TrafficModuleComponent


@Composable
fun StatisticElementCurrDevice(component: TrafficModuleComponent) {
    val model = remember { CountersModel() }

    rememberStopwatchTimer()

//    val stat = component.getStatistics() as Statistics
    val currTime = System.currentTimeMillis()
    val connectionTime =0L
//        if (stat.connectionStartTimeMs == 0L) 0L else currTime - stat.connectionStartTimeMs

    val (speedOut, speedIn) = Pair(0L, 0L)// model.getSpeed(currTime, stat.sendBytes, stat.receiveBytes)

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.75f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_traff_time),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 4.dp)
            )
            Text(
                text = connectionTime.toConnTimerFormat(
                    stringResource(id = R.string.tfs_counter_format)
                ),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textBlack
            )

            Text(
                text = stringResource(id = R.string.tfs_stat_time),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = hintTextColor
            )
        }

        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = tfsDivider
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_traff_speed),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 4.dp)
            )
            Row {
                Text(
                    text = speedOut.toHumanReadableByteCountSI,
                    textAlign = TextAlign.End,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = textBlack,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_traff_speed_up),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = speedIn.toHumanReadableByteCountSI,
                    textAlign = TextAlign.End,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = textBlack,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_traff_speed_down),
                    contentDescription = null
                )
            }

            Text(
                text = stringResource(id = R.string.tfs_stat_speed),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = hintTextColor
            )
        }

        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = tfsDivider
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.75f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_traff_total),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 4.dp)
            )
            Text(
                text = "stat",//(stat.sendBytes + stat.receiveBytes).toHumanReadableByteCountSI,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textBlack
            )

            Text(
                text = stringResource(id = R.string.tfs_stat_total),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = hintTextColor
            )
        }
    }
}

@Composable
fun StatisticElementAllDevices(outputTraffic: Long, inputTraffic: Long) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_traff_output),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = outputTraffic.toHumanReadableByteCountSI,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = tfsInputText
            )

            Text(
                text = stringResource(id = R.string.tfs_traffic_output),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = hintTextColor
            )
        }
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = tfsDivider
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_traff_input),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = inputTraffic.toHumanReadableByteCountSI,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = Color.Black
            )

            Text(
                text = stringResource(id = R.string.tfs_traffic_input),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = hintTextColor
            )
        }
    }
}



@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun StatisticPreviewAll() {
    RunVpnTheme {
        StatisticElementAllDevices(0, 0)
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun StatisticPreviewCurrent() {
    RunVpnTheme {
        StatisticElementCurrDevice(FakeTrafficModuleComponent())
    }
}
