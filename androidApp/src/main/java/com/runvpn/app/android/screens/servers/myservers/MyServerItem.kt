package com.runvpn.app.android.screens.servers.myservers

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.BorderedContainerView
import com.runvpn.app.core.ui.cardBackgroundColor
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.utils.TestData


@Composable
fun MyServerItem(
    customServer: Server,
    modifier: Modifier = Modifier,
    isCurrentServer: Boolean = false,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
) {

    val iconRes = remember(customServer.protocol) {
        return@remember when (customServer.protocol) {
            ConnectionProtocol.OPENVPN -> R.drawable.ic_protocol_openvpn
            ConnectionProtocol.WIREGUARD -> R.drawable.ic_protocol_wireguard
            ConnectionProtocol.IKEV2 -> R.drawable.ic_protocol_ikev2
            else -> R.drawable.ic_protocol_unspecified
        }
    }

    BorderedContainerView(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(52.dp),
        containerColor = if (isCurrentServer) {
            dividerColor
        } else {
            cardBackgroundColor
        }
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onEditClick(customServer.uuid) }
        ) {

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = customServer.name ?: stringResource(R.string.imported_server),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(R.string.server, customServer.protocol.name),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = hintTextColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!customServer.uuid.contains("uuid")) {
                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f),
                    onClick = { onEditClick.invoke(customServer.uuid) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        tint = Color.Unspecified,
                        contentDescription = "",
                    )
                }

                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f),
                    onClick = { onDeleteClick.invoke(customServer.uuid) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        tint = Color.Unspecified,
                        contentDescription = "",
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun CustomServerPreview() {
    MyServerItem(
        isCurrentServer = false,
        customServer = TestData.testServer1,
        onDeleteClick = {},
        onEditClick = {}
    )
}

