package com.runvpn.app.android.screens.servers.serverlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.utils.WidgetPreview
import com.runvpn.app.core.ui.textTertiaryColor
import com.runvpn.app.core.ui.tfsConnectedSimpleText
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.utils.TestData

@Composable
fun ServerItem(
    server: Server,
    onClick: (Server) -> Unit,
    onFavouriteClick: (Server, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.Transparent)
            .clickable { onClick(server) }
    ) {
        IconButton(
            onClick = { onFavouriteClick(server, !server.isFavorite) },
        ) {
            Icon(
                painter = painterResource(
                    id =
                    if (server.isFavorite) R.drawable.ic_favorite_new
                    else R.drawable.ic_favorite_new_inactive
                ),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.width(2.dp))

        Text(
            text = server.city ?: stringResource(id = R.string.unknown_city),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.server_filter_run_service),
            fontSize = 10.sp,
            lineHeight = 14.sp,
            color = textTertiaryColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(tfsConnectedSimpleText)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "37%",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color(0xFF87898F),
                maxLines = 1
            )
        }
    }
}

@WidgetPreview
@Composable
private fun ServerItemPreview() {
    RunVpnTheme {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            ServerItem(server = TestData.testServer1, onClick = {}, onFavouriteClick = { _, _ -> })
        }
    }
}
