package com.runvpn.app.android.screens.dns

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.feature.settings.dns.adddialog.CreateDnsServerDialogComponent
import com.runvpn.app.feature.settings.dns.adddialog.FakeCreateDnsServerDialogComponent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDnsServerDialog(
    component: CreateDnsServerDialogComponent
) {
    val state by component.state.subscribeAsState()

    AppBottomSheet(
        onDismiss = component::onDismissClicked,
        title = stringResource(id = R.string.creating_dns_server_dialog_title),
        sheetState = SheetState(
            skipPartiallyExpanded = true,
            density = LocalDensity.current,
            initialValue = SheetValue.Expanded
        ),
    ) {
        AppTextField(
            value = state.name,
            placeholder = stringResource(id = R.string.dns_server_name_placeholder),
            onValueChanged = component::onServerNameChanged,
            isError = state.isNameError,
            errorMessage = stringResource(id = R.string.error_enter_server_name),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.ip,
            placeholder = stringResource(id = R.string.dns_server_ip_placeholder),
            isError = state.isIpEmptyError || state.isIpFormatError,
            errorMessage = stringResource(
                id = if (state.isIpEmptyError) {
                    R.string.error_cannot_be_empty
                } else R.string.error_invalid_ip_address
            ),
            onValueChanged = component::onServerAddressChanged,
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (state.isCreatingError) {
            Text(
                text = stringResource(id = R.string.error_unexpected),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.isAlreadyCreatedError) {
            Text(
                text = stringResource(id = R.string.error_dns_already_created),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        AppButton(
            onClick = component::onAddServerClick,
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(text = stringResource(id = R.string.add_server))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
private fun AddDnsServerDialogPreview() {
    RunVpnTheme {
        AddDnsServerDialog(FakeCreateDnsServerDialogComponent())
    }
}
