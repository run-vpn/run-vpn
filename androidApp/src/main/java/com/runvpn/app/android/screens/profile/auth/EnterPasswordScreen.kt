package com.runvpn.app.android.screens.profile.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.toErrorMessage
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.AppToolbar2
import com.runvpn.app.android.widgets.message.noOverlapByMessage
import com.runvpn.app.data.device.domain.models.isValid
import com.runvpn.app.data.settings.domain.AppTheme
import com.runvpn.app.feature.authorization.enterpassword.EnterPasswordComponent
import com.runvpn.app.feature.authorization.enterpassword.FakeEnterPasswordComponent


@Composable
fun EnterPasswordScreen(component: EnterPasswordComponent) {
    val state by component.state.subscribeAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppToolbar2(
            onBackClick = component::onBackClick,
            title = stringResource(R.string.your_password),
            subtitle = stringResource(R.string.enter_your_password),
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.password,
            isError = state.passwordValidationResult != null && !state.passwordValidationResult!!.isValid,
            errorMessage = state.passwordValidationResult?.toErrorMessage(context),
            onValueChanged = component::onPasswordChanged,
            placeholder = stringResource(id = R.string.hint_password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        AppTextButton(
            text = stringResource(id = R.string.button_forgot_password),
            onClick = component::onForgotPasswordClick,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(
            modifier = Modifier
                .weight(1f)
        )

        if (!state.isSendCodeLoading) {
            AppTextButton(
                text = stringResource(id = R.string.action_enter_by_code),
                onClick = component::onAuthByEmailCodeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .noOverlapByMessage()
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppButton(
            onClick = component::onContinueClick,
            enabled = !state.isLoading && (state.passwordValidationResult?.isValid ?: false),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (!state.isLoading) {
                Text(text = stringResource(id = R.string.button_continue))
            } else {
                CircularProgressIndicator(modifier = Modifier.size(30.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(
    backgroundColor = 0xFFFFFFFF,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    device = "spec:parent=pixel_7",
    locale = "ru",
    showSystemUi = true,
    showBackground = true
)
@Composable
fun EditPasswordScreenPreview() {
    RunVpnTheme(appTheme = AppTheme.LIGHT) {
        EnterPasswordScreen(FakeEnterPasswordComponent())
    }
}
