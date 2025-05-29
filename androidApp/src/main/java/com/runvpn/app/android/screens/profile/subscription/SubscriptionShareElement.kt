package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.ElevatedContainerView
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.profileCardsBackgroundColor

@Composable
fun SubscriptionShareElement(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: Painter,
    onClick: () -> Unit
) {
    ElevatedContainerView(
        modifier = modifier,
        containerColor = profileCardsBackgroundColor
    ) {

        Column(modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp)) {

            Row {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = subtitle, fontSize = 12.sp, lineHeight = 16.sp, color = hintTextColor)
        }
    }
}


@Preview
@Composable
private fun SubscriptionShareElementPreview() {
    SubscriptionShareElement(
        title = "Код активации",
        subtitle = "Выслать код на активацию подписки",
        icon = painterResource(id = R.drawable.ic_key),
        onClick = {},
        modifier = Modifier.width(150.dp)
    )
}
