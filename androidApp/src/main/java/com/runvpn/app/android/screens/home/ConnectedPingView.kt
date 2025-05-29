package com.runvpn.app.android.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.core.ui.bgColorConnected
import com.runvpn.app.core.ui.textColorConnected

@Composable
fun ConnectedPingView(modifier: Modifier = Modifier, ping: Int) {
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(bgColorConnected)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_server_load_medium),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(id = R.string.connected),
            color = textColorConnected,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(id = R.string.ping, ping),
            color = textColorConnected,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}
