package com.runvpn.app.android.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.greenGradientEndColor
import com.runvpn.app.core.ui.greenGradientStartColor
import com.runvpn.app.core.ui.textDisabled
import com.runvpn.app.core.ui.textWhite

@Composable
fun AppUpdateBar(
    title: String,
    newVersion: String,
    currentVersion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        greenGradientStartColor,
                        greenGradientEndColor
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_update_bar),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(32.dp)
                .background(textWhite, RoundedCornerShape(8.dp))
                .padding(5.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = textWhite,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Row {

                Box(modifier = Modifier.height(30.dp), contentAlignment = Alignment.Center) {
                    VerticalDivider(
                        thickness = 0.4.dp,
                        modifier = Modifier,
                        color = textDisabled
                    )
                    Image(
                        painter = painterResource(id = R.drawable.img_luch),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .scale(1.6f),
                        contentScale = ContentScale.Fit,
                    )
                }



                Spacer(Modifier.width(8.dp))

                Column {
                    Text(
                        text = newVersion,
                        color = textWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = currentVersion,
                        color = textWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(textWhite, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = stringResource(id = R.string.update),
                color = greenColor,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Preview(locale = "ru-rRU", device = "id:pixel_2_xl")
@Composable
private fun AppUpdateBarPreview() {
    AppUpdateBar("Update downlaoded", "New version: 1.2.3", "Your version: 1.2.2", {})
}
