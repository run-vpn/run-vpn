package com.runvpn.app.android.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.utils.WidgetPreview
import com.runvpn.app.core.ui.appCheckBoxColor

@Composable
fun AppCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(uncheckedColor = appCheckBoxColor),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {

    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}


@WidgetPreview
@Composable
private fun AppCheckBoxCheckedPreview() {
    RunVpnTheme {
        AppCheckBox(checked = true, onCheckedChange = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AppCheckBoxUncheckedPreview() {
    RunVpnTheme {
        AppCheckBox(checked = false, onCheckedChange = {})
    }
}
