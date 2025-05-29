package com.runvpn.app.android.screens.profile.auth

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.toErrorMessage
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.data.device.domain.models.PasswordValidationResult
import com.runvpn.app.data.device.domain.models.isValid
import com.runvpn.app.feature.authorization.setpassword.FakeSetPasswordComponent
import com.runvpn.app.feature.authorization.setpassword.SetPasswordComponent

@Composable
fun SetPasswordScreen(component: SetPasswordComponent) {
    val state by component.state.subscribeAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = component::onCancelClick) {
            Image(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "")
        }
        Image(
            painter = painterResource(id = R.drawable.il_email_confirmed),
            contentDescription = "",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.padding(top = 32.dp))

        Text(
            text = stringResource(id = R.string.email_confirmed),
            fontSize = 32.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
        Text(
            text = stringResource(id = R.string.email_confirmed_desc),
            fontSize = 18.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
        AppTextField(
            value = state.password,
            onValueChanged = component::onPasswordChanged,
            placeholder = stringResource(id = R.string.new_password),
            isError = state.passwordValidationResult != null
                    && !state.passwordValidationResult!!.isValid
                    && state.passwordValidationResult !is
                    PasswordValidationResult.ErrorConfirmPasswordDoesNotMatch,
            errorMessage = state.passwordValidationResult?.toErrorMessage(context),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = state.confirmPassword,
            onValueChanged = component::onConfirmPasswordChanged,
            placeholder = stringResource(id = R.string.confirm_password),
            isError = state.passwordValidationResult is
                    PasswordValidationResult.ErrorConfirmPasswordDoesNotMatch,
            errorMessage = state.passwordValidationResult?.toErrorMessage(context),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        if (!state.isGeneratePasswordLoading) {
            AppTextButton(
                text = stringResource(id = R.string.generate_password_and_send_email),
                onClick = component::onGeneratePasswordAndSendToEmailClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        AppButton(
            onClick = component::onSavePasswordAndCloseClick,
            enabled = state.passwordValidationResult is PasswordValidationResult.Valid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.save_password_and_close))
        }
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
fun SetPasswordScreenPreview() {
    RunVpnTheme {
        SetPasswordScreen(FakeSetPasswordComponent())
    }
}
