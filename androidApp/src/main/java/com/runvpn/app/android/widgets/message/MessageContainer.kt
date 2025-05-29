package com.runvpn.app.android.widgets.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.getMessage
import com.runvpn.app.core.ui.textOrange
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.message.presentation.FakeMessageComponent
import com.runvpn.app.tea.message.presentation.MessageComponent

@Composable
fun MessageContainer(
    component: MessageComponent,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val visibleMessage by component.visibleMessage.subscribeAsState()
    val additionalBottomPadding = with(LocalDensity.current) {
        var result = LocalMessageOffsets.current.values.maxOrNull()?.toDp() ?: 0.dp
        val navbarsBottomPadding = WindowInsets.navigationBars
            .asPaddingValues()
            .calculateBottomPadding()
        val ime = WindowInsets.ime.asPaddingValues().calculateBottomPadding()

        if (result != 0.dp) {
            result -= navbarsBottomPadding
        }
        if (result >= ime) {
            result -= ime
        }

        result
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = bottomPadding + additionalBottomPadding)
    ) {
        visibleMessage.wrappedValue?.let {
            MessagePopup(
                message = it,
                onAction = component::onActionClick
            )
        }
    }
}

@Composable
private fun MessagePopup(
    message: AppMessage,
    onAction: () -> Unit,
) {
    val context = LocalContext.current

    Popup(
        alignment = Alignment.BottomCenter,
        properties = PopupProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            elevation = CardDefaults.cardElevation(3.dp),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .wrapContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(vertical = 13.dp, horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = message.getMessage(context),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                )
                MessageButton(
                    text = message.actionTitle ?: stringResource(R.string.close),
                    onClick = onAction
                )
            }
        }
    }
}


@Composable
private fun MessageButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textOrange
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun MessageUiPreview() {
    RunVpnTheme {
        MessageContainer(
            component = FakeMessageComponent(),
            bottomPadding = 40.dp,
            modifier = Modifier
        )
    }
}
