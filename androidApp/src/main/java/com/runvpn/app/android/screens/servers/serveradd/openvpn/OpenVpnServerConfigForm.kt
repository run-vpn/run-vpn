package com.runvpn.app.android.screens.servers.serveradd.openvpn

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getFileName
import com.runvpn.app.android.ext.readFile
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.AppTextFieldPassword
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.core.ui.underLineColor
import com.runvpn.app.feature.serveradd.openvpn.DefaultOpenVpnServerConfigComponent
import com.runvpn.app.feature.serveradd.openvpn.FakeOpenVpnServerConfigComponent
import com.runvpn.app.feature.serveradd.openvpn.OpenVpnServerConfigComponent

@Composable
fun OpenVpnServerConfigForm(
    component: OpenVpnServerConfigComponent, modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.getFileName(uri)?.let { fileName ->
            if (fileName.split(".")
                    .last() == DefaultOpenVpnServerConfigComponent.OVPN_FILE_EXTENSION
            ) {
                component.onConfigFileNameLoaded(fileName)

                context.readFile(uri).let { config ->
                    component.onReadConfigFromFile(config)
                }
                return@rememberLauncherForActivityResult
            }
        }
        component.onConfigFileNameLoaded("")
    }

    val state by component.state.subscribeAsState()

    Column(modifier = modifier) {
        AppTextField(
            value = state.username,
            onValueChanged = component::onLoginChange,
            placeholder = stringResource(R.string.username),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            isError = state.showUserPassError,
            errorMessage = stringResource(R.string.field_required)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextFieldPassword(
            value = state.password,
            onValueChanged = component::onPasswordChange,
            placeholder = stringResource(id = R.string.server_config_password),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            isError = state.showUserPassError,
            errorMessage = stringResource(R.string.field_required)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isInEditMode) return

        Divider(color = colorStrokeSeparator, modifier = Modifier.padding(16.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(48.dp)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        component::onImportModeChange.invoke(
                            OpenVpnServerConfigComponent.OpenVpnImportMode.URL
                        )
                    }) {
                Text(
                    text = stringResource(id = R.string.enter_link),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                if (state.ovpnImportMode == OpenVpnServerConfigComponent.OpenVpnImportMode.URL) Divider(
                    color = underLineColor,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp)
                        .height(2.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        component::onImportModeChange.invoke(
                            OpenVpnServerConfigComponent.OpenVpnImportMode.FILE
                        )
                    }) {
                Text(
                    text = stringResource(id = R.string.load_file),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                if (state.ovpnImportMode == OpenVpnServerConfigComponent.OpenVpnImportMode.FILE) Divider(
                    color = underLineColor,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp)
                        .height(2.dp)
                        .fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.ovpnImportMode == OpenVpnServerConfigComponent.OpenVpnImportMode.FILE) {
            AppTextField(value = state.configFileName,
                onValueChanged = {},
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { launcher.launch("application/octet-stream") }
                    .focusable(false),
                placeholder = stringResource(id = R.string.choose_config_file),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_file), null
                    )
                },
                enabled = false,
                isError = state.showHostError != null,
                errorMessage = stringResource(
                    id = when (state.showHostError) {
                        OpenVpnServerConfigComponent.ConfigError.READ_ERROR -> R.string.error_read_config_file
                        else -> R.string.error_invalid_config_file
                    }
                )
            )
        } else {
            AppTextField(
                value = state.ovpnUrlImport,
                onValueChanged = component::onImportUrlChange,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                placeholder = stringResource(R.string.enter_link),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_file), null
                    )
                },
                isError = state.showUrlError,
                errorMessage = stringResource(R.string.invalid_url)
            )
        }
    }
}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun OpenVpnServerConfigFormPreview() {
    OpenVpnServerConfigForm(component = FakeOpenVpnServerConfigComponent())
}
