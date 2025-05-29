package com.runvpn.app.android.screens.servers.serveradd

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.readFile
import com.runvpn.app.android.utils.ConfigUtils
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.feature.serveradd.ikev2.DefaultIkev2ConfigComponent.Companion.IKEV2_CERTIFICATE_FILE_MIME_TYPE
import com.runvpn.app.feature.serveradd.ikev2.FakeIkev2ConfigComponent
import com.runvpn.app.feature.serveradd.ikev2.Ikev2ConfigComponent

@Composable
fun Ikev2ServerConfigForm(component: Ikev2ConfigComponent, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.readFile(uri).let { certificate ->
            val name = ConfigUtils.getCertificateName(certificate)
            component.onCertificateLoaded(
                certificateName = name,
                certificate = certificate
            )
        }
        return@rememberLauncherForActivityResult
    }

    val state by component.state.subscribeAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        AppTextField(
            value = state.host,
            onValueChanged = component::onHostChange,
            placeholder = stringResource(R.string.server_config_host),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            isError = state.isHostError,
            errorMessage = stringResource(id = R.string.field_required)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.username,
            onValueChanged = component::onUsernameChange,
            placeholder = stringResource(R.string.username),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.password,
            onValueChanged = component::onPasswordChange,
            placeholder = stringResource(R.string.server_config_password),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(value = state.certificateName,
            onValueChanged = {},
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { launcher.launch(IKEV2_CERTIFICATE_FILE_MIME_TYPE) }
                .focusable(false),
            placeholder = stringResource(R.string.choose_certificate),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_file), null
                )
            },
            enabled = false,
            isError = state.isCertificateError,
            errorMessage = stringResource(R.string.invalid_certificate_file)
        )

    }

}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Ikev2ServerConfigFormPreview() {
    Ikev2ServerConfigForm(component = FakeIkev2ConfigComponent())
}
