package com.runvpn.app.android.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.utils.WidgetPreview
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.textInputBackgroundColor

@Composable
fun ClickableTextField(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    onClick: () -> Unit
) {

    AppTextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .focusable(false),
        value = value,
        onValueChanged = {},
        unfocusedContainerColor = textInputBackgroundColor,
        focusedContainerColor = textInputBackgroundColor,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        placeholder = placeholder,
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_bottom), null
            )
        },
        disabledPlaceholderColor = textBlack,
        disabledTextColor = textBlack,
        enabled = false
    )

}


@WidgetPreview
@Composable
private fun ClickableTextFieldPreview() {
    RunVpnTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ClickableTextField(
                value = "Clickable Text Field",
                placeholder = "Click to do something",
                onClick = {})
        }
    }
}

