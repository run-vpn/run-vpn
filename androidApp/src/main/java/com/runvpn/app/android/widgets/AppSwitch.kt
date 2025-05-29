package com.runvpn.app.android.widgets

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.utils.WidgetPreview
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.switchThumbColor
import com.runvpn.app.core.ui.switchUncheckedTrackColor

@Composable
fun AppSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Switch(
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = switchThumbColor,
            uncheckedTrackColor = switchUncheckedTrackColor,
            checkedThumbColor = colorIconAccent,
            checkedTrackColor = colorIconAccent,
            checkedTrackAlpha = 0.5f,
        ),
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .width(56.dp)
            .height(48.dp)
    )
}


@WidgetPreview
@Composable
private fun AppSwitchCheckedPreview() {
    AppSwitch(checked = true, onCheckedChange = {})
}

@WidgetPreview
@Composable
private fun AppSwitchUnCheckedPreview() {
    AppSwitch(checked = false, onCheckedChange = {})
}
