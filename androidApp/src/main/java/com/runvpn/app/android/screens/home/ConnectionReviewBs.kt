package com.runvpn.app.android.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.BorderedContainerView
import com.runvpn.app.android.widgets.RatingBar
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.core.ui.cardBackgroundColor
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textGrayColor
import com.runvpn.app.feature.home.rating.ConnectionReviewComponent
import com.runvpn.app.feature.home.rating.FakeConnectionReviewComponent
import com.murgupluoglu.flagkit.FlagKit
import com.runvpn.app.android.ext.getProtocolIcon
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionReviewBs(
    component: ConnectionReviewComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()

    AppBottomSheet(
        title = stringResource(R.string.rate_the_service),
        containerColor = cardBackgroundColor,
        onDismiss = component::onDismissClicked,
        modifier = modifier,
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = stringResource(id = R.string.connection_review_dialog_subtitle),
                fontSize = 18.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                color = textGrayColor,
                modifier = Modifier.padding(top = 36.dp, start = 32.dp, end = 32.dp)
            )

            RatingBar(
                rating = state.rating,
                onRatingChanged = component::onRatingChanged,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))


            AppTextField(
                value = state.feedbackMessage,
                placeholder = stringResource(R.string.your_comment),
                onValueChanged = component::onFeedbackMessageChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 250.dp)
                    .padding(horizontal = 16.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppButton(
                onClick = component::onSendReviewClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(stringResource(id = R.string.send))
            }

            Spacer(modifier = Modifier.height(74.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                BorderedContainerView(
                    strokeWidth = 1.dp,
                    modifier = Modifier
                        .height(60.dp)
                        .weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        RoundedImage(
                            painter = painterResource(
                                id = if (state.server.iso != null) {
                                    FlagKit.getResId(
                                        LocalContext.current, state.server.iso!!
                                    )
                                } else {
                                    getProtocolIcon(state.server.protocol)
                                }
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(40.dp, 28.dp)
                        )

                        Spacer(modifier = Modifier.padding(start = 8.dp))

                        Column {
                            Text(
                                text = stringResource(id = R.string.connection_review_dialog_title),
                                color = primaryColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${state.server.country ?: ""}  ${state.server.city ?: state.server.protocol}",
                                fontSize = 12.sp,
                                color = textGrayColor
                            )
                        }
                    }
                }

                /** временно отключено, до рефакторинга серверов и корректного обновления данных*/
//                Spacer(modifier = Modifier.width(12.dp))
//                BorderedContainerView(
//                    strokeWidth = 1.dp,
//                    modifier = Modifier.size(56.dp),
//                ) {
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_favourite_unchecked),
//                            contentDescription = null,
//                            tint = colorIconAccent,
//                            modifier = Modifier.size(25.dp)
//                        )
//                    }
//                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CompleteConnectionStatsView(
                stats = state.connectionStats,
                connectedServer = state.server,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF5E5E5E)
@Composable
fun ConnectionReviewBsPreview() {
    RunVpnTheme {
        ConnectionReviewBs(
            component = FakeConnectionReviewComponent()
        )
    }
}
