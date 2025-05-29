package com.runvpn.app.android.screens.profile.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.toTimerFormat
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.core.ui.appButtonContainerColor
import com.runvpn.app.core.ui.disabledButtonContainer
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.feature.authorization.confirmcode.ConfirmCodeComponent
import com.runvpn.app.feature.authorization.confirmcode.FakeConfirmCodeComponent

@Composable
fun ConfirmCodeScreen(component: ConfirmCodeComponent, modifier: Modifier = Modifier) {

    val state = component.state.subscribeAsState()
    val focusRequester = FocusRequester()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Row(modifier = Modifier.fillMaxWidth()) {

            IconButton(
                onClick = { component.onCancelClick() }, modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.confirm_code),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                val edit = buildAnnotatedString {
                    append(stringResource(id = R.string.confirm_code_sent, state.value.email))
                    append(" ")
                    withStyle(
                        style = SpanStyle(
                            color = primaryColor,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(stringResource(R.string.change))
                    }
                }

                ClickableText(
                    text = edit,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Gray
                    ),
                    onClick = { offset ->
                        component.onChangeEmailClick()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        BasicTextField(
            value = state.value.confirmCode,
            singleLine = true,
            onValueChange = component::onConfirmCodeChanged,
            enabled = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            decorationBox = {
                Row(Modifier.fillMaxWidth()) {
                    repeat(ConfirmCodeComponent.CONFIRM_CODE_LENGTH) { index ->
                        CharView(
                            index = index,
                            text = state.value.confirmCode,
                            isError = state.value.isConfirmCodeError,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            modifier = Modifier
                .wrapContentWidth()
                .focusRequester(focusRequester)
                .padding(horizontal = 16.dp)
        )

        if (state.value.isConfirmCodeError) {
            Text(
                text = stringResource(R.string.code_is_not_correct),
                color = textErrorColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = component::onRequestNewCode,
            enabled = state.value.requestCodeTime == 0L,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.Transparent,
                containerColor = Color.Transparent,
                contentColor = appButtonContainerColor,
                disabledContentColor = disabledButtonContainer
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text =
                if (state.value.requestCodeTime == 0L) {
                    stringResource(
                        id = R.string.request_new_code
                    )
                } else {
                    stringResource(
                        id = R.string.request_new_code_in,
                        state.value.requestCodeTime.toTimerFormat()
                    )
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.background(Color.Transparent)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppButton(
            onClick = component::onConfirmClick,
            enabled = state.value.isValidCode && !state.value.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (state.value.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp))
            } else {
                Text(text = stringResource(id = R.string.button_continue))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

}


@Composable
fun CharView(index: Int, text: String, isError: Boolean, modifier: Modifier) {
    Card(
        border = BorderStroke(
            2.dp,
            when {
                isError -> textErrorColor

                (index < text.length + 1) -> appButtonContainerColor

                else -> Color.Transparent
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor =
            if (index < text.length + 1) {
                Color.Transparent
            } else {
                textInputBackgroundColor
            }
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .padding(6.dp)
            .aspectRatio(1f)
    ) {
        Text(
            text = (text.getOrElse(index) { Character.MIN_VALUE }).toString(),
            modifier = modifier
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun ConfirmCodeScreenPreview() {
    ConfirmCodeScreen(FakeConfirmCodeComponent())
}

