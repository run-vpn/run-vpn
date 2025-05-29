package com.runvpn.app.android.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.byteToSizeRounded
import com.runvpn.app.android.ext.getProtocolName
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.core.ui.ultraLightBlue
import com.runvpn.app.data.connection.ConnectionStatisticsManager
import com.runvpn.app.data.servers.utils.TestData
import com.runvpn.app.data.common.models.Server

@Composable
fun CurrentSessionNetworkStatistics(
    stats: ConnectionStatisticsManager.ConnectionStats,
    ping: Int,
    currentServer: Server,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .border(2.dp, textWhite, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(ultraLightBlue)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            TrafficElement(
                icon = painterResource(id = R.drawable.ic_connected_protocol),
                traffic = stringResource(id = getProtocolName(currentServer.protocol)),
                description = stringResource(id = R.string.protocol),
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            )

            TrafficElement(
                icon = painterResource(id = R.drawable.ic_server_source),
                traffic = stringResource(id = R.string.app_name),
                description = stringResource(R.string.source),
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            )

            TrafficElement(
                icon = painterResource(id = R.drawable.ic_ping),
                traffic = ping.toString(),
                description = stringResource(id = R.string.ping_hint),
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            )
        }


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(textWhite)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            TrafficElement(
                icon = painterResource(id = R.drawable.ic_total_traffic),
                traffic = (stats.totalUploaded + stats.totalDownloaded).byteToSizeRounded(),
                description = stringResource(id = R.string.traffic_total),
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            )

            TrafficElement(
                icon = painterResource(id = R.drawable.ic_download_traffic),
                traffic = stats.downloadSpeedPerSecond.byteToSizeRounded() + "/s",
                description = stringResource(id = R.string.traffic_download),
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            )

            TrafficElement(
                icon = painterResource(id = R.drawable.ic_upload_traffic),
                traffic = stats.uploadSpeedPerSecond.byteToSizeRounded() + "/s",
                description = stringResource(id = R.string.traffic_upload),
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
            )
        }


    }


}


@Composable
private fun TrafficElement(
    icon: Painter,
    traffic: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Image(
            painter = icon,
            contentDescription = "",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = traffic,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = description,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                color = textHintColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(backgroundColor = 0xFF949494, showBackground = true)
@Composable
fun CurrentSessionNetworkStatsPreview() {
    RunVpnTheme {
        CurrentSessionNetworkStatistics(
            currentServer = TestData.testServer1,
            ping = 54,
            stats = ConnectionStatisticsManager.ConnectionStats(
                127_000L,
                48_000L,
                30_000L,
                40_000L,
                0L,
                ConnectionStatisticsManager.ConnectionStatsTimerType.Stopwatch
            )
        )
    }
}
