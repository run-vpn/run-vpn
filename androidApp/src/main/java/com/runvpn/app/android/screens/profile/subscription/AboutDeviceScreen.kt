package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.toDateLocalized
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.android.widgets.DeleteConfirmationElement
import com.runvpn.app.android.widgets.ItemListBottomSheet
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.core.ui.tfsConnectedSimpleText
import com.runvpn.app.core.ui.tfsConnectedText
import com.runvpn.app.data.subscription.domain.entities.Subscription
import com.runvpn.app.feature.subscription.aboutdevice.AboutDeviceComponent
import com.runvpn.app.feature.subscription.aboutdevice.FakeAboutDeviceComponent
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDeviceScreen(component: AboutDeviceComponent) {
    val context = LocalContext.current
    val state by component.state.subscribeAsState()

    var showDeviceRemoveBs by remember { mutableStateOf(false) }
    var showSubscriptionRemoveBs by remember { mutableStateOf(false) }
    var showChooseSubscriptionBs by remember { mutableStateOf(false) }

    var isNameInputFocused by remember { mutableStateOf(false) }
    val nameFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AppToolBar(
            onBackClick = { dispatchOnBackPressed(context) },
            title = stringResource(R.string.about_device),
            subtitle = stringResource(R.string.about_device_desc)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppTextField(
            value = state.deviceName ?: stringResource(id = R.string.dev_unknown),
            placeholder = stringResource(R.string.device_name),
            onValueChanged = component::onDeviceNameChange,
            focusedContainerColor = textWhite,
            unfocusedContainerColor = textWhite,
            textStyle = TextStyle(
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold
            ),
            trailingIcon = {
                when {
                    state.isNameChangeLoading -> CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )

                    isNameInputFocused ->
                        Row {
                            IconButton(onClick = {
                                component.onSaveDeviceNameClick()
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_done),
                                    contentDescription = "",
                                    tint = Color.Unspecified
                                )
                            }
                            IconButton(onClick = {
                                component.onCancelDeviceNameEditingClick()
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_cancel),
                                    contentDescription = "",
                                    tint = Color.Unspecified
                                )
                            }
                        }

                    else -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "",
                            tint = Color.Unspecified
                        )
                    }
                }
            },
            keyboardActions = KeyboardActions(onDone = {
                component.onSaveDeviceNameClick()
                focusManager.clearFocus()
            }),
            isError = state.isNameError,
            errorMessage = stringResource(id = R.string.field_required),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(2.dp, lightBlue, RoundedCornerShape(8.dp))
                .focusRequester(nameFocusRequester)
                .onFocusChanged { isNameInputFocused = it.isFocused }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .border(2.dp, lightBlue, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            DeviceInfoItem(
                title = stringResource(R.string.type),
                description = state.deviceDto.software.name
                    ?: stringResource(id = R.string.dev_unknown)
            )

            HorizontalDivider(color = dividerColor)

            DeviceInfoItem(
                title = stringResource(R.string.model),
                description = state.deviceDto.hardware.name
                    ?: stringResource(id = R.string.dev_unknown)
            )

            HorizontalDivider(color = dividerColor)

            DeviceInfoItem(
                title = stringResource(R.string.ip),
                description = state.deviceDto.activity?.ip
                    ?: stringResource(id = R.string.dev_unknown)
            )

            HorizontalDivider(color = dividerColor)

            DeviceInfoItem(
                title = stringResource(id = R.string.last_online),
                description = state.deviceDto.activity?.createdAt?.toDateLocalized()
                    ?: stringResource(R.string.dev_unknown)
            )

            HorizontalDivider(color = dividerColor)

            DeviceInfoItem(
                title = stringResource(id = R.string.subscription_status),
                description = if (state.isSubscriptionActive) {
                    stringResource(id = R.string.subscription_status_active)
                } else {
                    stringResource(id = R.string.subscription_status_not_active)
                },
                descriptionColor = if (state.isSubscriptionActive) {
                    tfsConnectedText
                } else {
                    tfsConnectedSimpleText
                }
            )

            if (state.isSubscriptionActive) {
                AppButton(
                    cornerSize = 4.dp,
                    containerColor = lightBlue,
                    contentColor = colorIconAccent,
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                    onClick = component::onSubscriptionMoveClick,
                    modifier = Modifier
                        .height(30.dp)
                        .align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(id = R.string.move_subscription_to_other_device),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(!state.isSubscriptionActive && !state.isLoading) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(2.dp, lightBlue, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                AppTextField(modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showChooseSubscriptionBs = true }
                    .focusable(false),
                    value = if (state.selectedSubscription != null) {
                        stringResource(
                            id = R.string.subscription_period,
                            state.selectedSubscription?.periodInDays.toString()
                        ) + " " + pluralStringResource(
                            id = R.plurals.days_format,
                            count = state.selectedSubscription?.periodInDays ?: 0
                        )
                    } else {
                        stringResource(id = R.string.subscription_not_selected)
                    },
                    onValueChanged = {},
                    unfocusedContainerColor = textInputBackgroundColor,
                    focusedContainerColor = textInputBackgroundColor,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    placeholder = stringResource(R.string.choose_subscription),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_bottom), null
                        )
                    },
                    disabledPlaceholderColor = textBlack,
                    disabledTextColor = textBlack,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    onClick = component::onSubscriptionActivateClick,
                    containerColor = colorIconAccent,
                    contentColor = textWhite,
                    enabled = !state.isSubscriptionActivationLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isSubscriptionActivationLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    } else {
                        Text(text = stringResource(id = R.string.activate_subscription))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    onClick = component::onPromoClick,
                    containerColor = Color.Transparent,
                    contentColor = colorIconAccent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.activate_subscription_promo))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(state.isSubscriptionActive && !state.isLoading) {
            AppButton(
                onClick = component::onPromoClick,
                containerColor = lightBlue,
                contentColor = colorIconAccent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = stringResource(R.string.activate_subscription_promo))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))


        if (state.isRemoveDeviceAvailable) {
            AppButton(
                onClick = { showDeviceRemoveBs = true },
                containerColor = textErrorColor,
                contentColor = textWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = stringResource(R.string.remove_device))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

    }

    if (showDeviceRemoveBs) {
        AppBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            onDismiss = { showDeviceRemoveBs = false },
            title = stringResource(id = R.string.remove_device)
        ) {
            DeleteConfirmationElement(message = stringResource(R.string.remove_device_desc),
                onConfirm = {
                    component.onDeviceRemoveClick()
                    showDeviceRemoveBs = false
                },
                onDismiss = { showDeviceRemoveBs = false })
        }
    }

    if (showSubscriptionRemoveBs) {
        AppBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            onDismiss = { showSubscriptionRemoveBs = false },
            title = stringResource(R.string.move_subscription)
        ) {
            DeleteConfirmationElement(message = stringResource(R.string.remove_subscription_desc),
                onConfirm = {
                    component.onSubscriptionMoveClick()
                    showSubscriptionRemoveBs = false
                },
                onDismiss = { showSubscriptionRemoveBs = false })
        }
    }

    if (showChooseSubscriptionBs) {
        ItemListBottomSheet(title = stringResource(id = R.string.choose_subscription),
            items = state.subscriptions,
            onItemClick = component::onSubscriptionChoose,
            onDismiss = { showChooseSubscriptionBs = false }) {
            SubscriptionSelectItem(subscription = it)
        }
    }
}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun AboutDeviceScreenPreview() {
    AboutDeviceScreen(FakeAboutDeviceComponent(false))
}

@Preview
@Composable
private fun ChooseSubscriptionBsPreview() {
    val list = listOf(
        Subscription(
            id = "8097a852-9236-429b-b98e-ac907b1b8601",
            deviceUuid = "8097a852-9236-429b-b98e-ac907b1b8601",
            periodInDays = 3,
            expirationAt = Clock.System.now(),
            finishedAt = null
        ),
        Subscription(
            id = "8097a852-9236-429b-b98e-ac907b1b8601",
            deviceUuid = "8097a852-9236-429b-b98e-ac907b1b8601",
            periodInDays = 3,
            expirationAt = Clock.System.now(),
            finishedAt = null
        ),
    )

    RunVpnTheme {
        ItemListBottomSheet(title = stringResource(id = R.string.choose_subscription),
            items = list,
            onItemClick = {},
            onDismiss = {}) {
            SubscriptionSelectItem(subscription = it)
        }
    }
}
