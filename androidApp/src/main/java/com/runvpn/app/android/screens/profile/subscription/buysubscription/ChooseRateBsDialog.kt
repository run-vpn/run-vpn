package com.runvpn.app.android.screens.profile.subscription.buysubscription

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.toCurrencyFormat
import com.runvpn.app.android.rubikFontFamily
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.BorderedContainerView
import com.runvpn.app.android.widgets.BottomSheetTitle
import com.runvpn.app.android.widgets.IconLabel
import com.runvpn.app.android.widgets.ValuesSlider
import com.runvpn.app.core.ui.bottomSheetScrimColor
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textLightGrayColor
import com.runvpn.app.feature.subscription.tariff.ChooseRateComponent
import com.runvpn.app.feature.subscription.tariff.FakeChooseRateComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseRateBsDialog(component: ChooseRateComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()
    val cryptoDialog by component.payWithCryptoDialogChild.subscribeAsState()

    ModalBottomSheet(
        onDismissRequest = component::onDismissClicked,
        containerColor = Color.White,
        scrimColor = bottomSheetScrimColor,
        sheetState = SheetState(
            skipPartiallyExpanded = true,
            density = LocalDensity.current,
            initialValue = SheetValue.Expanded
        ),
    ) {
        BottomSheetTitle(
            title = stringResource(id = R.string.buy_subscription_title),
            onDismiss = component::onDismissClicked
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sliders
        BorderedContainerView(
            strokeWidth = 2.dp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                RateSlider(
                    title = stringResource(id = R.string.buy_sub_device_count),
                    iconPainter = painterResource(id = R.drawable.ic_device),
                    value = state.deviceCount,
                    sortedValues = state.availableDeviceCount.toList(),
                    onValueChange = {
                        component.onDeviceChanged(it.toInt())
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                RateSlider(
                    title = stringResource(id = R.string.buy_sub_days_count),
                    iconPainter = painterResource(id = R.drawable.ic_subscription_days),
                    value = state.daysCount,
                    sortedValues = state.allAvailableTariffs.map { it.periodInDays }.toSortedSet().toList(),
                    onValueChange = { component.onDaysChanged(it.toInt()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Inputs
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            InfoTextField(
                label = stringResource(id = R.string.buy_sub_devices),
                value = if (state.deviceCount == 0) "" else state.deviceCount.toString(),
                onValueChange = {
                    val value = it.toIntOrNull()
                    component.onDeviceChanged(value ?: 0)
                },
                modifier = Modifier
                    .weight(1f)
            )

            HorizontalDivider(
                thickness = 2.dp,
                color = dividerColor,
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .width(20.dp)
            )

            InfoTextField(
                label = stringResource(id = R.string.buy_sub_days),
                value = if (state.daysCount == 0) "" else state.daysCount.toString(),
                onValueChange = {
                    val value = it.toIntOrNull()
                    component.onDaysChanged(value ?: 0)
                },
                modifier = Modifier
                    .weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isRateNotFound) {
            Text(
                text = stringResource(id = R.string.rate_not_found),
                color = textErrorColor,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = textHintColor)) {
                        append(stringResource(id = R.string.buy_sub_cost))
                        append(": ")
                    }
                    withStyle(SpanStyle(color = textBlack)) {
                        append(state.cost.toFloat().toCurrencyFormat())
                    }
                },
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AppButton(
            onClick = component::onBuyClicked,
            enabled = !state.isLoading && !state.isRateNotFound,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.buy, state.cost))

            Spacer(modifier = Modifier.width(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator(
                    color = primaryColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun RateSlider(
    title: String,
    iconPainter: Painter,
    value: Int,
    sortedValues: List<Int>,
    onValueChange: (Float) -> Unit
) {
    IconLabel(
        text = title,
        painterStart = iconPainter
    )
    Spacer(modifier = Modifier.height(8.dp))
    ValuesSlider(
        values = sortedValues,
        selectedValue = value,
        onValueSelected = {
            onValueChange(it.toFloat())
        }
    )

    Spacer(modifier = Modifier.height(4.dp))

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        sortedValues.forEach {
            Text(
                text = it.toString(),
                fontSize = 14.sp,
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun InfoTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        label = {
            Text(
                text = label,
                fontFamily = rubikFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textLightGrayColor
            )
        },
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = modifier
            .border(2.dp, dividerColor, RoundedCornerShape(8.dp))
    )
}

@Preview(locale = "ru")
@Composable
private fun ChooseTariffBsDialogPreview() {
    RunVpnTheme {
        ChooseRateBsDialog(component = FakeChooseRateComponent())
    }
}
