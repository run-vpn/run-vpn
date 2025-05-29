package com.runvpn.app.android.screens.settings.connection.splittunneling

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.runvpn.app.android.R
import com.runvpn.app.data.settings.domain.SplitTunnelingApplication

@Composable
fun SplitTunnelingApplicationItem(
    application: SplitTunnelingApplication,
    icon: AsyncImagePainter,
    modifier: Modifier = Modifier,
    onClick: (SplitTunnelingApplication) -> Unit,
    actionIcon: Painter = painterResource(id = R.drawable.ic_add_to_split_tunneling)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(50.dp)
            .fillMaxWidth()
            .clickable { onClick.invoke(application) }
    ) {
        Image(
            icon,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .fillMaxHeight()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = application.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = actionIcon,
            contentDescription = null,
            tint = Color.Unspecified
        )

    }

}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SplitTunnelingApplicationItemPreview() {
    SplitTunnelingApplicationItem(
        application = SplitTunnelingApplication(
            "asdhas",
            "Google Chrome", false
        ),
        icon = rememberAsyncImagePainter(model = painterResource(id = R.drawable.ic_tab_settings)),
        onClick = {}
    )
}
