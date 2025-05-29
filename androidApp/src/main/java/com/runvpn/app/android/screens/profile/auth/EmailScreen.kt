package com.runvpn.app.android.screens.profile.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.core.ui.hintContainerBackgroundColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.feature.authorization.email.EmailComponent
import com.runvpn.app.feature.authorization.email.FakeEmailComponent

@Composable
fun EmailScreen(component: EmailComponent, modifier: Modifier = Modifier) {

    val state = component.state.subscribeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(top = 16.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            IconButton(
                onClick = { component.onCancelClick() }, modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }

            Image(
                painter = painterResource(id = R.drawable.vpnrun),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(id = R.string.email_screen_title),
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .wrapContentSize()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.email_screen_subtitle),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = hintTextColor,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .wrapContentSize()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            AppTextField(
                value = state.value.email,
                onValueChanged = component::onEmailChanged,
                placeholder = stringResource(id = R.string.hint_email),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                onClick = component::onConfirmEmailClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.value.isEmailValid && !state.value.isLoading
            ) {
                if (state.value.isLoading) {
                    CircularProgressIndicator(
                        color = primaryColor,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Text(text = stringResource(id = R.string.button_login_or_register))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                ) {
                    drawRect(colorStrokeSeparator)
                }

                Text(
                    text = stringResource(R.string.login_with),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = textHintColor
                )

                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                ) {
                    drawRect(colorStrokeSeparator)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                containerColor = Color.Transparent,
                onClick = component::onAuthByGoogleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, color = primaryColor, RoundedCornerShape(12.dp)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = stringResource(id = R.string.continue_with_google),
                    color = primaryColor,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AppButton(
                containerColor = Color.White,
                onClick = component::onAuthByFacebookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, color = primaryColor, RoundedCornerShape(12.dp)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_facebook),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = stringResource(id = R.string.continue_with_facebook),
                    color = primaryColor,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AppButton(
                containerColor = Color.White,
                onClick = component::onAuthByTelegramClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, color = primaryColor, RoundedCornerShape(12.dp)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_telegram),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = stringResource(id = R.string.continue_with_telegram),
                    color = primaryColor,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(hintContainerBackgroundColor, RoundedCornerShape(8.dp))
            ) {
                val context = LocalContext.current
                val uriHandler = LocalUriHandler.current

                val hintWithUri = buildAnnotatedString {
                    append(context.getString(R.string.email_screen_hint))
                    pushStringAnnotation(
                        "uri",
                        context.getString(R.string.email_screen_hint_uri)
                    )
                    append("\n")
                    withStyle(style = SpanStyle(color = primaryColor)) {
                        append(context.getString(R.string.email_screen_hint_uri))
                    }
                }
                ClickableText(
                    text = hintWithUri,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    onClick = { offset ->
                        if (hintWithUri.getStringAnnotations(
                                tag = "uri",
                                start = offset,
                                end = offset
                            )
                                .isNotEmpty()
                        ) {
                            uriHandler.openUri(context.getString(R.string.email_screen_hint_uri))
                        }
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun EmailScreenPreview() {
    EmailScreen(component = FakeEmailComponent())
}

