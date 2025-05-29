package com.runvpn.app.android.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.core.ui.hintTextColor

@Composable
fun AppToolBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    iconBack: Painter = painterResource(id = R.drawable.ic_close),
    title: String,
    subtitle: String,
) {
    Row(
        modifier = modifier
            .padding(top = 16.dp)
            .padding(horizontal = 4.dp)
            .fillMaxWidth()
    ) {

        IconButton(
            onClick = onBackClick, modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = iconBack,
                tint = Color.Unspecified,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = hintTextColor,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AppToolbarPreview() {
    RunVpnTheme {
        AppToolBar(onBackClick = { /*TODO*/ }, title = "Title", subtitle = "Subtitle")
    }
}
