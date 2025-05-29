package com.runvpn.app.android.screens.settings.support

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.feature.settings.support.reportproblem.FakeReportProblemComponent
import com.runvpn.app.feature.settings.support.reportproblem.ReportProblemComponent

@Composable
fun ReportProblemScreen(component: ReportProblemComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

        AppToolBar(
            onBackClick = { component.onBack() },
            iconBack = painterResource(id = R.drawable.ic_arrow_back),
            title = stringResource(id = R.string.create_ticket),
            subtitle = stringResource(
                id = R.string.sup_prob_desc
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppTextField(
            value = state.email,
            placeholder = stringResource(R.string.hint_enter_your_email),
            onValueChanged = component::onEmailChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.message,
            placeholder = stringResource(R.string.describe_your_problem),
            onValueChanged = component::onMessageChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 250.dp)
                .padding(horizontal = 16.dp),
            singleLine = false
        )

        Spacer(modifier = Modifier.weight(1f))

        AppButton(
            onClick = component::onSendClick,
            enabled = state.isEmailValid && state.message.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.send))
        }

    }
}

@Preview(showSystemUi = false, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ReportScreenPreview() {
    ReportProblemScreen(component = FakeReportProblemComponent())
}
