package com.runvpn.app.android.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.getIconResId
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.data.servers.utils.TestData
import com.runvpn.app.data.common.models.Server

@Composable
fun FavouriteServerItem(
    server: Server,
    onServerClicked: (Server) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .width(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onServerClicked(server) }
            .padding(12.dp)
    ) {
        RoundedImage(
            painter = painterResource(server.getIconResId(LocalContext.current)),
            contentDescription = null,
            modifier = Modifier
                .width(30.dp)
                .height(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = server.country ?: server.name ?: server.host,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight(500),
            textAlign = TextAlign.Start,
        )
    }
}

@Preview(backgroundColor = 0xFFEFF2F5, showBackground = true)
@Composable
fun FavoriteServerItemPreview() {
    RunVpnTheme {
        FavouriteServerItem(
            server = TestData.testServer1,
            onServerClicked = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
