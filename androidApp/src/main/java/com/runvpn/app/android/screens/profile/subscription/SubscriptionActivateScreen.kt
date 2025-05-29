package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.screens.home.sharedialog.ShareQrCodeBottomSheet
import com.runvpn.app.android.screens.profile.subscription.dialogs.ShareActivationCodeBottomSheet
import com.runvpn.app.android.screens.profile.subscription.dialogs.ShareApkFileBottomSheet
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.android.widgets.ConfirmationElement
import com.runvpn.app.android.widgets.ItemListBottomSheet
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.feature.common.dialogs.shareqrcode.ShareQrCodeComponent
import com.runvpn.app.feature.subscription.activate.FakeSubscriptionActivateComponent
import com.runvpn.app.feature.subscription.activate.SubscriptionActivateComponent
import com.runvpn.app.feature.subscription.activate.shareapkdialog.ShareApkFileComponent
import com.runvpn.app.feature.subscription.activate.sharecodedialog.ShareActivationCodeComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionActivateScreen(
    component: SubscriptionActivateComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    val dialog by component.dialog.subscribeAsState()


    val context = LocalContext.current

    var showChooseDeviceBottomSheet by remember { mutableStateOf(false) }
    var showChooseSubscriptionBottomSheet by remember { mutableStateOf(false) }
    var showConfirmMergeBottomSheet by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        AppToolBar(
            onBackClick = { dispatchOnBackPressed(context) },
            title = stringResource(R.string.activate_subscription),
            subtitle = stringResource(R.string.activate_subscription_decs)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.subscription_activate_hint),
            color = colorIconAccent,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .background(lightBlue, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .border(2.dp, lightBlue, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.choose_device_desc),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = textHintColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        if (state.allDevices.size > 1) {
                            showChooseDeviceBottomSheet = true
                        }
                    }
                    .focusable(false),
                value = state.selectedDevice?.fullName ?: "",
                onValueChanged = {},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                placeholder = stringResource(R.string.choose_device),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_bottom), null
                    )
                },
                disabledPlaceholderColor = textBlack,
                disabledTextColor = textBlack,
                enabled = false
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .border(2.dp, lightBlue, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.choose_your_subscription),
                fontSize = 12.sp,
                color = textHintColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        if (state.subscriptions.size > 1) {
                            showChooseSubscriptionBottomSheet = true
                        }
                    }
                    .focusable(false),
                value = if (state.selectedSubscription != null) {
                    stringResource(
                        id = R.string.subscription_period,
                        state.selectedSubscription?.periodInDays ?: 0
                    ) + " " + pluralStringResource(
                        id = R.plurals.days_format,
                        count = state.selectedSubscription?.periodInDays ?: 0
                    )
                } else { "" },
                onValueChanged = {},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                placeholder = stringResource(id = R.string.choose_subscription),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_bottom), null
                    )
                },
                disabledPlaceholderColor = textBlack,
                disabledTextColor = textBlack,
                enabled = false
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.weight(1f))

        AppButton(
            onClick = {
                if (state.selectedDevice?.latestSubscriptionUuid != null) {
                    showConfirmMergeBottomSheet = true
                } else {
                    component.onActivateClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = (state.selectedDevice?.uuid.isNullOrEmpty()
                .not() && state.selectedSubscription?.id.isNullOrEmpty().not())
        ) {
            Text(text = stringResource(id = R.string.activate))
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (showConfirmMergeBottomSheet) {
            AppBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                onDismiss = { showConfirmMergeBottomSheet = false },
                title = stringResource(R.string.confirm_activation)
            ) {
                ConfirmationElement(
                    message = stringResource(R.string.confirm_merge_desc),
                    onConfirm = {
                        component.onActivateClick()
                        showConfirmMergeBottomSheet = false
                    }
                )
            }
        }

        if (showChooseDeviceBottomSheet) {
            ItemListBottomSheet(
                onDismiss = { showChooseDeviceBottomSheet = false },
                title = stringResource(R.string.your_devices),
                items = state.allDevices,
                onItemClick = component::onChooseDevice
            ) {
                DeviceSelectItem(device = it)
            }
        }

        if (showChooseSubscriptionBottomSheet) {
            ItemListBottomSheet(
                items = state.subscriptions,
                onDismiss = { showChooseSubscriptionBottomSheet = false },
                onItemClick = component::onChooseSubscription,
                title = stringResource(id = R.string.choose_subscription)
            ) {
                SubscriptionSelectItem(subscription = it)
            }

        }
    }

    when (val child = dialog.child?.instance) {

        is ShareApkFileComponent -> {
            ShareApkFileBottomSheet(component = child)
        }

        is ShareQrCodeComponent -> {
            ShareQrCodeBottomSheet(component = child)
        }

        is ShareActivationCodeComponent -> {
            ShareActivationCodeBottomSheet(component = child)
        }

    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, locale = "ru-rRU")
@Composable
private fun SubscriptionActivateScreenPreview() {
    SubscriptionActivateScreen(FakeSubscriptionActivateComponent())
}
