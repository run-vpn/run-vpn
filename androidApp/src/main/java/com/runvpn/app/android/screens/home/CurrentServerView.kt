package com.runvpn.app.android.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.getIconResId
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.core.ui.textLightGrayColor
import com.runvpn.app.core.ui.textPrimaryColor
import com.runvpn.app.data.servers.utils.TestData
import com.runvpn.app.data.common.models.Server

@Composable
fun CurrentServerItem(
    server: Server,
    onServerClicked: (Server) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onServerClicked(server) }
    ) {
        RoundedImage(
            painter = painterResource(server.getIconResId(LocalContext.current)),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .width(40.dp)
                .height(28.dp)
        )
        Column(
            horizontalAlignment = Alignment.Start, modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = server.country ?: server.name ?: server.host,
                maxLines = 1,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = textPrimaryColor,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = server.city ?: server.host,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                color = textLightGrayColor,
                overflow = TextOverflow.Ellipsis,
            )
        }
        content()
    }

}

@Preview
@Composable
fun CurrentServerViewPreview() {
    RunVpnTheme {
        CurrentServerItem(server = TestData.testServer1, onServerClicked = {}) {
            
        }
    }
}
