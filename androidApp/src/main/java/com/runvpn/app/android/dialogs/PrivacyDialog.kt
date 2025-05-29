package com.runvpn.app.android.dialogs

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.ScrollBarConfig
import com.runvpn.app.android.ext.verticalScrollWithScrollbar
import com.runvpn.app.android.screens.welcome.WelcomeHintView
import com.runvpn.app.android.utils.Constants
import com.runvpn.app.android.widgets.AndroidHtmlViewer
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.screens.welcome.UseModeView
import com.runvpn.app.core.ui.bottomSheetScrimColor
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.core.ui.textLightGrayColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.data.settings.domain.AppUseMode
import com.runvpn.app.feature.home.trafficover.ChooseUsageModeComponent
import com.runvpn.app.feature.home.trafficover.FakeChooseUsageModeComponent

@Composable
fun PrivacyDialog(component: ChooseUsageModeComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()
    val showAllTerms = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bottomSheetScrimColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {}
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(textWhite)
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp)
        ) {

            Text(
                text = stringResource(R.string.our_rules_and_usage_mode),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .weight(1f)
                    .verticalScrollWithScrollbar(
                        rememberScrollState(),
                        scrollbarConfig = ScrollBarConfig(
                            padding = PaddingValues(4.dp, 4.dp, 4.dp, 4.dp),
                            indicatorColor = colorIconAccent,
                            alpha = 1.0f
                        )
                    )
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.welcome_our_vpn).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.welcome_our_vpn_desk),
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = textLightGrayColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(color = colorStrokeSeparator)

                Spacer(modifier = Modifier.height(8.dp))

                WelcomeHintView()

            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                UseModeView(
                    title = stringResource(R.string.variant_free),
                    subtitle = stringResource(id = R.string.variant_free_desc),
                    isChecked = state.selectedUseMode == AppUseMode.FREE,
                    onClick = { component.onUsageModeClick(AppUseMode.FREE) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                UseModeView(
                    title = stringResource(R.string.variant_paid),
                    subtitle = stringResource(R.string.variant_paid_desc),
                    isChecked = state.selectedUseMode == AppUseMode.PAID,
                    onClick = { component.onUsageModeClick(AppUseMode.PAID) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    onClick = component::onConfirmClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.save_changes))
                }

                Spacer(modifier = Modifier.height(8.dp))

                AppButton(
                    onClick = { showAllTerms.value = !showAllTerms.value },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Transparent
                ) {
                    Text(
                        text = stringResource(R.string.see_full_terms),
                        color = colorIconAccent
                    )
                }

                AnimatedVisibility(visible = showAllTerms.value) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.terms_of_use),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AndroidHtmlViewer(Constants.PRIVACY_FILE)
                }
            }
        }
    }

}


@Preview(
    device = "id:pixel_2", showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun TrafficOverDialogPreview() {
    PrivacyDialog(FakeChooseUsageModeComponent())
}
