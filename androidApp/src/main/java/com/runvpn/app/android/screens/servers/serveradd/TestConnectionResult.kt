package com.runvpn.app.android.screens.servers.serveradd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.core.ui.bgConnectionError
import com.runvpn.app.core.ui.bgConnectionSuccess
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.testConnectionButtonContainer
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.testConnectionSuccess
import com.runvpn.app.data.connection.ConnectionStatus


@Composable
fun TestConnectionResultConnecting(message: String, modifier: Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(testConnectionButtonContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_connection_success),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(25.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.test_connection_connecting),
                color = colorIconAccent,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                color = colorIconAccent,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TestConnectionResultSuccess(modifier: Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgConnectionSuccess)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_connection_success),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(25.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.test_connection_success),
                color = testConnectionSuccess,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                lineHeight = 18.sp
            )
            Text(
                text = stringResource(R.string.your_ip_address),
                color = testConnectionSuccess,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun TestConnectionResultError(message: String, modifier: Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgConnectionError)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(25.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.test_connection_error, message),
                color = textErrorColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                lineHeight = 18.sp
            )
            Text(
                text = stringResource(id = R.string.test_connection_retry),
                color = textErrorColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun TestConnectionResult(connectionStatus: ConnectionStatus?, modifier: Modifier = Modifier) {
    when (connectionStatus) {
        is ConnectionStatus.Error -> TestConnectionResultError(
            message = connectionStatus.message ?: "", modifier = modifier
        )

        is ConnectionStatus.Connected -> TestConnectionResultSuccess(modifier = modifier)

        is ConnectionStatus.Connecting -> TestConnectionResultConnecting(
            message = connectionStatus.statusMessage ?: "", modifier = modifier
        )

        else -> TestConnectionResultConnecting(
            message = stringResource(R.string.connection_status_can_t_be_provided),
            modifier = modifier
        )
    }
}


@Preview
@Composable
private fun TestConnectionTestConnectionResultConnectingPreview() {
    TestConnectionResult(connectionStatus = ConnectionStatus.Connecting("connecting"))
}

@Preview
@Composable
private fun TestConnectionTestConnectionResultErrorPreview() {
    TestConnectionResult(connectionStatus = ConnectionStatus.Error("Error"))
}

@Preview
@Composable
private fun TestConnectionTestConnectionResultConnectedPreview() {
    TestConnectionResult(connectionStatus = ConnectionStatus.Connected)
}
