package com.runvpn.app.android.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.core.ui.backgroundColor
import com.runvpn.app.core.ui.dividerColor


@Composable
fun BorderedContainerView(
    modifier: Modifier = Modifier,
    containerColor: Color = backgroundColor,
    strokeColor: Color = dividerColor,
    strokeWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {

    Card(
        colors = CardDefaults.outlinedCardColors(
            containerColor = containerColor,
        ),
        border = if (strokeWidth == 0.dp) null else BorderStroke(
            width = strokeWidth,
            color = strokeColor
        ),
        modifier = modifier
    ) {
        content()
    }

}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BorderContainerViewPreview() {
    RunVpnTheme {
        BorderedContainerView(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("BorderedContainerView")
                Text("BorderedContainerView")
                Text("BorderedContainerView")
            }
        }
    }
}
