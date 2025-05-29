package com.runvpn.app.android.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.boundingRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.conditional
import com.runvpn.app.android.ext.toTimerFormat
import com.runvpn.app.android.rubikFontFamily
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.timeoutToggleVpnViewIndicatorGradient
import com.runvpn.app.core.ui.toggleVpnBackgroundOffColor
import com.runvpn.app.core.ui.toggleVpnBackgroundOnColor
import com.runvpn.app.core.ui.toggleVpnBorderErrorColorEnd
import com.runvpn.app.core.ui.toggleVpnBorderErrorColorStart
import com.runvpn.app.core.ui.toggleVpnIconInactiveColor
import com.runvpn.app.core.ui.toggleVpnOffTextColor
import com.runvpn.app.core.ui.toggleVpnViewIndicatorGradient
import com.runvpn.app.data.connection.ConnectionStatus
import kotlin.math.min

const val DEFAULT_CONNECTING_TIMEOUT = 10_000
const val DEFAULT_POSITIVE_PROGRESS_TIMEOUT = 3_000

@Composable
fun ToggleVpnView(
    vpnStatus: ConnectionStatus,
    connectionTime: Long,
    modifier: Modifier = Modifier,
    timeoutMillis: Int = DEFAULT_CONNECTING_TIMEOUT,
    errorTime: Long = 3L,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    val alignment by animateHorizontalAlignmentAsState(vpnStatusToPosValue(vpnStatus))
    val backgroundColor by animateColorAsState(
        if (vpnStatus is ConnectionStatus.Connected) toggleVpnBackgroundOnColor else toggleVpnBackgroundOffColor,
        label = "background"
    )

    // region Animation

    val progressAnimation = remember {
        Animatable(if (vpnStatus is ConnectionStatus.Connected) 1f else 0f)
    }

    val timeoutAnimation = remember {
        Animatable(0f)
    }

    val progressGradientBrush = Brush.linearGradient(
        colors = toggleVpnViewIndicatorGradient
    )
    val errorGradientBrush = Brush.linearGradient(
        colors = timeoutToggleVpnViewIndicatorGradient
    )

    LaunchedEffect(vpnStatus) {
        if (vpnStatus is ConnectionStatus.Connecting) {
            progressAnimation.animateTo(1f, tween(DEFAULT_POSITIVE_PROGRESS_TIMEOUT))
            timeoutAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(timeoutMillis - DEFAULT_POSITIVE_PROGRESS_TIMEOUT)
            )
        } else {
            if (vpnStatus is ConnectionStatus.Connected) {
                if (progressAnimation.value != 1f) {
                    progressAnimation.animateTo(1f)
                }
                timeoutAnimation.snapTo(0f)
            } else {
                progressAnimation.snapTo(0f)
                timeoutAnimation.snapTo(0f)
            }
        }
    }

    // endregion

    Column(
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .wrapContentSize()
            .widthIn(min = 250.dp)
            .height(106.dp)
            .padding(4.dp)
            .conditional(
                vpnStatus is ConnectionStatus.Error
            ) {
                Modifier.border(
                    4.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            toggleVpnBorderErrorColorStart,
                            toggleVpnBorderErrorColorEnd
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
            }
            .conditional(
                vpnStatus is ConnectionStatus.Connecting || vpnStatus is ConnectionStatus.Connected
            ) {
                Modifier
                    .drawWithCache {
                        val cornerRadius = min(size.width, size.height) * 0.5f

                        val progressPath = makeRoundRectPath(rect, cornerRadius)
                        val errorPath = makeRoundRectPath(rect, cornerRadius)

                        onDrawWithContent {
                            val progressSegmentedPath = Path()
                            val errorSegmentedPath = Path()

                            val progressPathMeasure = PathMeasure().apply {
                                setPath(progressPath, true)
                            }
                            val errorPathMeasure = PathMeasure().apply {
                                setPath(errorPath, true)
                            }

                            progressPathMeasure.getSegment(
                                0f,
                                progressAnimation.value * progressPathMeasure.length,
                                progressSegmentedPath
                            )
                            errorPathMeasure.getSegment(
                                0f,
                                timeoutAnimation.value * errorPathMeasure.length,
                                errorSegmentedPath
                            )

                            drawPath(
                                path = progressSegmentedPath,
                                brush = progressGradientBrush,
                                style = Stroke(
                                    width = 24.dp.value
                                )
                            )
                            drawPath(
                                path = errorSegmentedPath,
                                brush = errorGradientBrush,
                                style = Stroke(
                                    width = 24.dp.value
                                )
                            )
                            drawContent()
                        }
                    }
            }
            .clip(RoundedCornerShape(50))
            .drawBehind {
                drawRect(backgroundColor)
            }
            .clickable(indication = null, interactionSource = remember {
                MutableInteractionSource()
            }) {
                if (vpnStatus.isDisconnected()) {
                    onConnectClick()
                } else if (vpnStatus is ConnectionStatus.Connected || vpnStatus is ConnectionStatus.Connecting) {
                    onDisconnectClick()
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (vpnStatus is ConnectionStatus.Connected) {
                Text(
                    textAlign = TextAlign.Start,
                    text = connectionTime.toTimerFormat(),
                    fontSize = 28.sp,
                    color = hintTextColor,
                    fontWeight = FontWeight(500),
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(6.dp)
                    .width(90.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (vpnStatus.isDisconnected()) Color.White
                        else primaryColor
                    )
            ) {
                if (vpnStatus is ConnectionStatus.Connecting) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_two_arrow_right),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(
                            if (vpnStatus is ConnectionStatus.Connected) Color.White
                            else toggleVpnIconInactiveColor
                        ),
                        modifier = Modifier
                            .size(40.dp)
                            .rotate(if (vpnStatus is ConnectionStatus.Connected) -180f else 0f)
                    )
                }
            }

            if (vpnStatus.isDisconnected()) {
                if (errorTime > 0L) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Text(
                            text = "$errorTime",
                            fontSize = 40.sp,
                            lineHeight = 40.sp,
                            maxLines = 1,
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            ),
                            fontFamily = rubikFontFamily,
                            color = greenColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.height(40.dp)
                        )
                        Text(
                            text = stringResource(R.string.in_time),
                            fontSize = 16.sp,
                            color = hintTextColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                } else {
                    Text(
                        text = "OFF",
                        fontSize = 40.sp,
                        fontWeight = FontWeight(500),
                        color = toggleVpnOffTextColor,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }

}

private fun makeRoundRectPath(rect: Rect, cornerRadius: Float): Path {
    val path = Path()

    with(path) {
        val newPath = this
            .asAndroidPath()
            .apply {
                addRoundRect(
                    RoundRect(
                        rect,
                        CornerRadius(cornerRadius)
                    ).boundingRect.toAndroidRectF(),
                    cornerRadius,
                    cornerRadius,
                    android.graphics.Path.Direction.CW
                )

                val mMatrix = android.graphics.Matrix()
                mMatrix.postScale(-1f, -1f, rect.width / 2, rect.height / 2)
                transform(mMatrix)

                close()
            }
        addPath(newPath.asComposePath())
    }

    return path
}

private val CacheDrawScope.rect: Rect
    get() {
        return Rect(0f, 0f, size.width, size.height)
    }

private fun vpnStatusToPosValue(vpnStatus: ConnectionStatus): Float {
    val positionDisconnected = -1f
    val positionConnecting = 0f
    val positionConnected = 1f
    val positionError = -1f

    return when (vpnStatus) {
        is ConnectionStatus.Disconnected -> positionDisconnected
        is ConnectionStatus.Connecting -> positionConnecting
        is ConnectionStatus.Connected -> positionConnected
        is ConnectionStatus.Error -> positionError
        is ConnectionStatus.Paused -> positionDisconnected
        is ConnectionStatus.Idle -> positionDisconnected
    }
}

@Preview(name = "Disconnected")
@Composable
fun ToggleVpnPreview() {
    RunVpnTheme {
        ToggleVpnView(
            vpnStatus = ConnectionStatus.Disconnected,
            connectionTime = 1000L,
            onConnectClick = { /*TODO*/ }
        ) {

        }
    }
}

@Preview(name = "Connecting")
@Composable
fun ToggleVpnConnectingPreview() {
    RunVpnTheme {
        ToggleVpnView(
            vpnStatus = ConnectionStatus.Connecting(""),
            connectionTime = 0L,
            onConnectClick = { /*TODO*/ }
        ) {

        }
    }
}

@Preview(name = "Connected")
@Composable
fun ToggleVpnConnectedWithHoursPreview() {
    RunVpnTheme {
        ToggleVpnView(
            vpnStatus = ConnectionStatus.Connected,
            connectionTime = 3600L,
            onConnectClick = { /*TODO*/ }
        ) {

        }
    }
}

@Preview(name = "Connected")
@Composable
fun ToggleVpnConnectedPreview() {
    RunVpnTheme {
        ToggleVpnView(
            vpnStatus = ConnectionStatus.Connected,
            connectionTime = 600L,
            onConnectClick = { /*TODO*/ }
        ) {

        }
    }
}


@Preview(name = "Error")
@Composable
fun ToggleVpnErrorPreview() {
    RunVpnTheme {
        ToggleVpnView(
            vpnStatus = ConnectionStatus.Error(""),
            connectionTime = 180L,
            onConnectClick = { /*TODO*/ }
        ) {

        }
    }
}


@Preview(name = "Idle")
@Composable
fun ToggleVpnIdlePreview() {
    RunVpnTheme {
        ToggleVpnView(
            vpnStatus = ConnectionStatus.Idle(),
            connectionTime = 180L,
            onConnectClick = { /*TODO*/ }
        ) {

        }
    }
}
