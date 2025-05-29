package com.runvpn.app.android.screens.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.runvpn.app.android.utils.Constants
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AndroidHtmlViewer
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.data.settings.domain.AppUseMode
import com.runvpn.app.feature.home.welcome.FakeWelcomeComponent
import com.runvpn.app.feature.home.welcome.WelcomeComponent

@Composable
fun PrivacyScreen(component: WelcomeComponent, modifier: Modifier = Modifier) {

    val showAllTerms = remember { mutableStateOf(false) }
    val state by component.state.subscribeAsState()

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(textWhite)
            .verticalScroll(rememberScrollState())
    ) {

        Column {
            Box {
                WelcomeScreenTopBar(
                    image = painterResource(id = R.drawable.img_welcome_top),
                    title = stringResource(id = R.string.www_vpn_run),
                    subtitle = stringResource(R.string.terms_and_use_modes)
                )
                if (state.hasCloseButton) {
                    IconButton(
                        onClick = { dispatchOnBackPressed(context) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            tint = Color.Unspecified,
                            contentDescription = null
                        )
                    }
                }
            }
        }

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
                .padding(horizontal = 14.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.welcome_our_vpn).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.welcome_our_vpn_desk),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = hintTextColor
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
                Text(text = stringResource(R.string.agree_and_continue))
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

@Composable
private fun WelcomeScreenTopBar(
    image: Painter,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillBounds
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = textWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = subtitle, color = textWhite)
        }
    }
}


@Preview(
    showBackground = true, backgroundColor = 0xFFFFFFFF,
    device = "id:pixel_2_xl"
)
@Composable
private fun WelcomeScreenPreview() {
    PrivacyScreen(FakeWelcomeComponent())
}
