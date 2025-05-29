package com.runvpn.app.android.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.byteToSizeRounded
import com.runvpn.app.android.ext.toTimerFormat
import com.runvpn.app.android.widgets.BorderedContainerView
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.data.connection.domain.ConnectionStatistics
import com.runvpn.app.data.servers.utils.TestData
import com.runvpn.app.data.common.models.Server

@Composable
fun CompleteConnectionStatsView(
    stats: ConnectionStatistics, connectedServer: Server, modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
    ) {
        StatsItem(
            icon = painterResource(id = R.drawable.ic_connection_last_ip),
            value = connectedServer.host,
            description = stringResource(id = R.string.connection_stats_last_ip),
            modifier = Modifier.weight(1f)
        )

        StatsItem(
            icon = painterResource(id = R.drawable.ic_connection_duration),
            value = (stats.durationInMillis / 1000).toTimerFormat(),
            description = stringResource(id = R.string.connection_stats_duration),
            modifier = Modifier.weight(1f)
        )

        StatsItem(
            icon = painterResource(id = R.drawable.ic_connection_total_traffic),
            value = (stats.totalBytesUploaded + stats.totalBytesDownloaded).byteToSizeRounded(),
            description = stringResource(id = R.string.traffic),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatsItem(
    icon: Painter,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    BorderedContainerView(
        strokeWidth = 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = "",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = textHintColor,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ConnStatsPrev() {
    RunVpnTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CompleteConnectionStatsView(
                stats = ConnectionStatistics(
                    totalBytesDownloaded = 1049088,
                    totalBytesUploaded = 1019088,
                    durationInMillis = 38_000
                ),
                connectedServer = TestData.testServer1,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}
