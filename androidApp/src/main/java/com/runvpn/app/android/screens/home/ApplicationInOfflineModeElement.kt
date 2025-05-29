package com.runvpn.app.android.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.ElevatedClickableContainerView
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.core.network.NetworkApiFactory
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.sliderInactiveColor
import com.runvpn.app.tea.utils.Timer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ApplicationInOfflineModeElement(modifier: Modifier = Modifier, onClick: () -> Unit) {
    var currentSecond by remember { mutableLongStateOf(0L) }
    var repeatTime by remember { mutableIntStateOf(0) }
    val timer = Timer.countdown(NetworkApiFactory.RECONNECT_PERIOD_MILLIS)
    LaunchedEffect(key1 = "ReconnectTimer") {
        launch {
            timer.currentMillisFlow.collectLatest {
                currentSecond = it / 1000L
                if (currentSecond == 0L) {
                    repeatTime++
                    if (repeatTime < NetworkApiFactory.TOTAL_RECONNECT_REPEAT_ATTEMPTS) {
                        timer.startTimer(this)
                    }
                }
            }
        }
        launch {
            timer.startTimer(this)
        }
    }

    ElevatedClickableContainerView(modifier = modifier, onClick = onClick, radius = 16.dp) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedImage(
                painter = painterResource(id = R.drawable.ic_cloud_offline),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .background(sliderInactiveColor, RoundedCornerShape(100f))
                    .padding(5.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.app_works_offline),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (currentSecond > 0L) {
                    Text(
                        text = stringResource(R.string.reconnecting_in__seconds, currentSecond),
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        color = hintTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = null,
                tint = colorIconAccent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun DomainNotReachableElementPreview() {
    ApplicationInOfflineModeElement(onClick = {})
}
