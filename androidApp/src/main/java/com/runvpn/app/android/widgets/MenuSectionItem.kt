package com.runvpn.app.android.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.core.ui.menuHintTextColor

@Composable
fun MenuSectionItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isEnabled: Boolean = true,
    isElevated: Boolean = true,
    painterStart: Painter? = null,
    painterEnd: Painter = painterResource(id = R.drawable.arrow_right),
    onClick: () -> Unit
) {
    if (isElevated) {
        ElevatedContainerView(modifier = modifier) {
            MenuSectionItemContent(
                title = title,
                description = description,
                modifier = Modifier
                    .alpha(if (isEnabled) 1.0f else 0.5f)
                    .clickable { if (isEnabled) onClick() },
                painterStart = painterStart,
                painterEnd = painterEnd
            )
        }
    } else {
        MenuSectionItemContent(
            title = title,
            description = description,
            modifier = Modifier
                .alpha(if (isEnabled) 1.0f else 0.5f)
                .clickable { if (isEnabled) onClick() },
            painterStart = painterStart,
            painterEnd = painterEnd
        )
    }
}

@Composable
private fun MenuSectionItemContent(
    modifier: Modifier,
    title: String,
    description: String,
    painterStart: Painter?,
    painterEnd: Painter
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()

    ) {

        painterStart?.let {
            Icon(
                painter = painterStart,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .fillMaxHeight()
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = title, fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                color = menuHintTextColor
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            painter = painterEnd,
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEDEDED)
@Composable
fun MenuSectionItemPreview() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(80.dp)
    ) {
        MenuSectionItem(
            title = stringResource(R.string.always_on_vpn),
            description = stringResource(R.string.always_on_vpn_desc),
            isElevated = true,
            onClick = {},
            painterStart = painterResource(id = R.drawable.ic_sup_faq),
            painterEnd = painterResource(id = R.drawable.ic_chevron_right),
        )
    }
}
