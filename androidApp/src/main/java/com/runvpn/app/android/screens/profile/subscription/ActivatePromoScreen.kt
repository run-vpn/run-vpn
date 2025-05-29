package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.feature.subscription.promo.ActivatePromoComponent
import com.runvpn.app.feature.subscription.promo.FakeActivatePromoComponent

@Composable
fun ActivatePromoScreen(modifier: Modifier = Modifier, component: ActivatePromoComponent) {

    val context = LocalContext.current
    val state by component.state.subscribeAsState()

    Column(modifier = modifier.fillMaxSize()) {
        AppToolBar(
            onBackClick = { dispatchOnBackPressed(context) },
            title = stringResource(R.string.activate_promo_for_device),
            subtitle = "",
            iconBack = painterResource(
                id = R.drawable.ic_arrow_back
            )
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .border(2.dp, lightBlue, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.promo,
                onValueChanged = component::onPromoChanged,
                unfocusedContainerColor = textInputBackgroundColor,
                focusedContainerColor = textInputBackgroundColor,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                placeholder = stringResource(R.string.enter_promo)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                stringResource(R.string.enter_promo_hint),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = textHintColor
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AppButton(
            onClick = component::onActivateClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(R.string.activate))
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, locale = "ru-rRU")
@Composable
private fun ActivatePromoPreview() {
    ActivatePromoScreen(component = FakeActivatePromoComponent(promo = "test"))
}
