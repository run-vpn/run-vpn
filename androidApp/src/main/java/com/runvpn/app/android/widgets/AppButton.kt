package com.runvpn.app.android.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.core.ui.appButtonContainerColor
import com.runvpn.app.core.ui.disabledButtonContainer
import com.runvpn.app.core.ui.textDisabled
import com.runvpn.app.core.ui.textWhite

@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = appButtonContainerColor,
    disabledContainerColor: Color = disabledButtonContainer,
    contentColor: Color = textWhite,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    disabledContentColor: Color = textDisabled,
    cornerSize: Dp = 12.dp,
    containerHeight: Dp = 48.dp,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        content = content,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = disabledContainerColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor
        ),
        contentPadding = contentPadding,
        shape = RoundedCornerShape(cornerSize),
        enabled = enabled,
        modifier = modifier
            .height(containerHeight)
    )
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AppButtonPreview() {
    RunVpnTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            AppButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("App Button")
            }
        }
    }
}
