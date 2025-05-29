package com.runvpn.app.android.screens.servers.serveradd.wireguard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.data.connection.models.WireGuardPeer
import com.runvpn.app.feature.serveradd.wireguard.WireGuardConfigComponent

@Composable
fun WireGuardPeerView(
    component: WireGuardConfigComponent,
    index: Int,
    item: WireGuardPeer,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.peer), fontSize = 16.sp)

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { component.onDeletePeer(index) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            value = item.publicKey,
            placeholder = stringResource(R.string.public_key),
            onValueChanged = { component.onPeerPublicKeyChange(index, it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            value = item.preSharedKey,
            placeholder = stringResource(R.string.preshared_key),
            onValueChanged = { component.onPeerSharedKeyChange(index, it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            value = item.endpoint,
            placeholder = stringResource(R.string.endpoint),
            onValueChanged = { component.onPeerEndpointChange(index, it) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
