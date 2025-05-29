package com.runvpn.app.android.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.core.ui.textSecondaryColor
import kotlin.math.roundToInt


@Composable
fun AppToolbar2(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    subtitle: String? = null
) {
    var titleStartOffset by remember {
        mutableFloatStateOf(0f)
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onBackClick?.invoke() }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                modifier = Modifier
                    .onGloballyPositioned {
                        titleStartOffset = it.positionInParent().x
                    }
            )
        }
        subtitle?.let {
            Text(
                text = subtitle,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                color = textSecondaryColor,
                modifier = Modifier
                    .absoluteOffset {
                        IntOffset(x = titleStartOffset.roundToInt() + 4, y = 0)
                    }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFEFEFE)
@Composable
fun AppToolbar2Preview() {
    RunVpnTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            AppToolbar2(
                title = "Title",
                subtitle = "Subtitle",
                onBackClick = {}
            )
        }
    }
}
