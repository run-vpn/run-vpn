package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.toDateLocalized
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.core.ui.bgConnectionError
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.feature.subscription.confirm.FakeSubscriptionConfirmComponent
import com.runvpn.app.feature.subscription.confirm.SubscriptionConfirmComponent

@Composable
fun SubscriptionConfirmScreen(
    component: SubscriptionConfirmComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        AppToolBar(
            onBackClick = { dispatchOnBackPressed(context) },
            title = stringResource(R.string.confirm_subscription),
            subtitle = stringResource(R.string.confirm_subscription_desc),
            iconBack = painterResource(id = R.drawable.ic_arrow_back)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = state.device.fullName ?: stringResource(id = R.string.dev_unknown),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            DeviceInfoItem(
                title = stringResource(R.string.type),
                description = state.device.software.platformName
                    ?: stringResource(id = R.string.dev_unknown)
            )

            HorizontalDivider(color = dividerColor)

            DeviceInfoItem(
                title = stringResource(R.string.model),
                description = state.device.hardware.name
                    ?: stringResource(id = R.string.dev_unknown)
            )

            HorizontalDivider(color = dividerColor)

            DeviceInfoItem(
                title = stringResource(R.string.ip),
                description = state.device.activity?.ip
                    ?: stringResource(id = R.string.dev_unknown)
            )

            HorizontalDivider(color = dividerColor)

            DeviceInfoItem(
                title = stringResource(id = R.string.last_online),
                description = state.device.activity?.createdAt?.toDateLocalized()
                    ?: stringResource(R.string.dev_unknown)
            )

            HorizontalDivider(color = dividerColor)

            DeviceInfoItem(
                title = stringResource(R.string.subcription_active_until),
                description = "23.04.2024"
            )

            HorizontalDivider(color = dividerColor)
        }

        Spacer(modifier = Modifier.weight(1f))

        AppButton(
            onClick = component::onConfirmClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = !state.isLoading
        ) {
            Text(text = stringResource(R.string.confirm))
        }

        Spacer(modifier = Modifier.height(12.dp))

        AppButton(
            onClick = { dispatchOnBackPressed(context) },
            contentColor = textErrorColor,
            containerColor = bgConnectionError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(R.string.refuse))
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun SubscriptionCompleteScreenPreview() {
    SubscriptionConfirmScreen(FakeSubscriptionConfirmComponent())
}
