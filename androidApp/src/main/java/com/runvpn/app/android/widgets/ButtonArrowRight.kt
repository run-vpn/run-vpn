package com.runvpn.app.android.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.R
import com.runvpn.app.android.utils.WidgetPreview
import com.runvpn.app.core.ui.dividerColor

@Composable
fun ButtonArrowRight(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.arrow_right),
        contentDescription = "",
        modifier = modifier
            .size(24.dp)
            .background(dividerColor, RoundedCornerShape(4.dp))
            .padding(2.dp)
            .clickable { onClick() }
    )
}


@WidgetPreview
@Composable
private fun ButtonArrowRightPreview() {
    ButtonArrowRight(onClick = { /*TODO*/ })
}
