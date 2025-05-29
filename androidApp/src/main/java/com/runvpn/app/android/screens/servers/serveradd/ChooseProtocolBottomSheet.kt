package com.runvpn.app.android.screens.servers.serveradd

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.runvpn.app.android.ext.getProtocolIcon
import com.runvpn.app.android.ext.getProtocolName
import com.runvpn.app.core.ui.bottomSheetScrimColor
import com.runvpn.app.core.ui.cardBackgroundColor
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.data.common.models.ConnectionProtocol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseProtocolBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onItemClick: (ConnectionProtocol) -> Unit,
    onFaqClick: (ConnectionProtocol) -> Unit,
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = cardBackgroundColor,
        scrimColor = bottomSheetScrimColor,
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) {
        Box {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.choose_protocol),
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProtocolView(
                    protocol = ConnectionProtocol.XRAY,
                    protocolDesc = stringResource(id = R.string.protocol_description_xray),
                    onItemClick = onItemClick,
                    onDismiss = onDismiss,
                    onFaqClick = onFaqClick
                )

                HorizontalDivider(color = colorStrokeSeparator)

                ProtocolView(
                    protocol = ConnectionProtocol.OPENVPN,
                    protocolDesc = stringResource(id = R.string.protocol_description_openvpn),
                    onItemClick = onItemClick,
                    onDismiss = onDismiss,
                    onFaqClick = onFaqClick
                )

                HorizontalDivider(color = colorStrokeSeparator)

                ProtocolView(
                    protocol = ConnectionProtocol.WIREGUARD,
                    protocolDesc = stringResource(id = R.string.protocol_description_wireguard),
                    onItemClick = onItemClick,
                    onDismiss = onDismiss,
                    onFaqClick = onFaqClick
                )

                HorizontalDivider(color = colorStrokeSeparator)

                ProtocolView(
                    protocol = ConnectionProtocol.IKEV2,
                    protocolDesc = stringResource(id = R.string.protocol_description_ikev2),
                    onItemClick = onItemClick,
                    onDismiss = onDismiss,
                    onFaqClick = onFaqClick
                )

                HorizontalDivider(color = colorStrokeSeparator)

                ProtocolView(
                    protocol = ConnectionProtocol.OVERSOCKS,
                    protocolDesc = stringResource(id = getProtocolName(ConnectionProtocol.OVERSOCKS)),
                    onItemClick = onItemClick,
                    onDismiss = onDismiss,
                    onFaqClick = onFaqClick
                )

                Spacer(modifier = Modifier.height(80.dp))

            }
        }
    }
}


@Composable
fun ProtocolView(
    protocol: ConnectionProtocol,
    protocolDesc: String = "",
    onDismiss: () -> Unit,
    onItemClick: (ConnectionProtocol) -> Unit,
    onFaqClick: (ConnectionProtocol) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onDismiss.invoke()
                onItemClick.invoke(protocol)
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = getProtocolIcon(protocol)),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = stringResource(id = getProtocolName(protocol)),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = protocolDesc,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                color = textHintColor,
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stringResource(R.string.faq_setup_own_server),
                fontSize = 12.sp,
                color = primaryColor,
                modifier = Modifier.clickable { onFaqClick.invoke(protocol) }
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ChooseProtocolBottomSheetPreview() {
    ChooseProtocolBottomSheet(
        sheetState = SheetState(
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Expanded
        ),
        onFaqClick = {},
        onDismiss = {},
        onItemClick = {}
    )

}
