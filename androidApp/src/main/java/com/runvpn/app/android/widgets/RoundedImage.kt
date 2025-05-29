package com.runvpn.app.android.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.R

@Composable
fun RoundedImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerSize: Dp = 4.dp,
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .clip(RoundedCornerShape(cornerSize))
    )
}


@Preview(showBackground = true, backgroundColor = 0xFFC6C6C6)
@Composable
private fun RoundedImagePreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        RoundedImage(
            painter = painterResource(id = R.drawable.ic_protocol_unspecified),
            contentDescription = null,
            cornerSize = 10.dp,
            modifier = Modifier.size(50.dp)
        )
    }
}
