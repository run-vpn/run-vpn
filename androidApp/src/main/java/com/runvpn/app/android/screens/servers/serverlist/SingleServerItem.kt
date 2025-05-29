package com.runvpn.app.android.screens.servers.serverlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getIconResId
import com.runvpn.app.android.ext.getProtocolName
import com.runvpn.app.core.ui.cardBackgroundColor
import com.runvpn.app.core.ui.currentServerBackgroundColor
import com.runvpn.app.core.ui.disabledTextHintColor
import com.runvpn.app.core.ui.textSecondaryColor
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.utils.TestData


@Composable
fun SingleServerItem(
    server: Server,
    onFavouriteClick: (Server, Boolean) -> Unit,
    onClick: (Server) -> Unit,
    modifier: Modifier = Modifier,
    isCurrentServer: Boolean = false
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                if (isCurrentServer) {
                    currentServerBackgroundColor
                } else {
                    cardBackgroundColor
                }
            )
            .clickable {
                onClick(server)
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        IconButton(
            onClick = { onFavouriteClick(server, !server.isFavorite) },
            modifier = Modifier.size(48.dp)
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

        Image(
            painter = painterResource(server.getIconResId(LocalContext.current)),
            contentDescription = null,
            contentScale = if (server.isMine) ContentScale.Fit else ContentScale.FillBounds,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .width(40.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = server.name ?: server.country
                ?: stringResource(R.string.imported_server),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Text(
                text = stringResource(R.string.ip_, server.host),
                color = textSecondaryColor,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Text(
            text = stringResource(getProtocolName(server.protocol)),
            color = disabledTextHintColor,
            fontSize = 12.sp
        )
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun CustomServerPreview() {
    SingleServerItem(
        isCurrentServer = false,
        server = TestData.testServer1,
        onFavouriteClick = { server, bool ->

        },
        onClick = {}
    )
}

