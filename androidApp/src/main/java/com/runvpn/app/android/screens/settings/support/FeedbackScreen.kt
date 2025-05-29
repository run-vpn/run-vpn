package com.runvpn.app.android.screens.settings.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.android.widgets.RatingBar
import com.runvpn.app.core.ui.textGrayColor
import com.runvpn.app.feature.settings.support.feedback.FakeFeedbackComponent
import com.runvpn.app.feature.settings.support.feedback.FeedbackComponent

@Composable
fun FeedbackScreen(component: FeedbackComponent, modifier: Modifier = Modifier) {

    val state by component.state.subscribeAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

        AppToolBar(
            onBackClick = { component.onBack() },
            iconBack = painterResource(id = R.drawable.ic_arrow_back),
            title = stringResource(id = R.string.sup_rate_title),
            subtitle = stringResource(
                id = R.string.sup_rate_desc
            )
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.hint_feedback_screen),
                color = textGrayColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RatingBar(
                rating = state.rating,
                onRatingChanged = component::onRatingChanged,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 38.dp)
            )
        }

        AppButton(
            onClick = component::onSendClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.send))
        }

    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, locale = "ru-rRU",
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun FeedbackScreenPreview() {
    FeedbackScreen(component = FakeFeedbackComponent())
}
