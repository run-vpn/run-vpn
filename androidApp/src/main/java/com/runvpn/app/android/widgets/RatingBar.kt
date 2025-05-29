package com.runvpn.app.android.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme

@Composable
fun RatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
) {
    Row(modifier = modifier) {
        (1..maxRating).forEach {
            Image(
                painter = painterResource(
                    id = if (it <= rating) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                ),
                contentDescription = "",
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .weight(1f)
                    .clickable(indication = null, interactionSource = remember {
                        MutableInteractionSource()
                    }) { onRatingChanged(it) }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun RatingBarPreview() {
    RunVpnTheme {
        RatingBar(rating = 4, onRatingChanged = {})
    }
}
