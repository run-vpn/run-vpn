package com.runvpn.app.android.screens.settings.support

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.R
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.android.widgets.MenuSectionItem
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.feature.settings.support.main.FakeSupportComponent
import com.runvpn.app.feature.settings.support.main.SupportComponent


@Composable
fun SupportScreen(component: SupportComponent, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        AppToolBar(
            onBackClick = { dispatchOnBackPressed(context) },
            iconBack = painterResource(id = R.drawable.ic_arrow_back),
            title = stringResource(id = R.string.support),
            subtitle = stringResource(
                id = R.string.support_desc
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        MenuSectionItem(
            title = stringResource(id = R.string.sup_faq_title),
            description = stringResource(id = R.string.sup_faq_desc),
            onClick = { component.onFaqClick() },
            painterStart = painterResource(id = R.drawable.ic_sup_faq),
            painterEnd = painterResource(id = R.drawable.ic_chevron_right),
            isElevated = false
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        MenuSectionItem(
            title = stringResource(id = R.string.sup_prob_title),
            description = stringResource(id = R.string.sup_prob_desc),
            onClick = { component.onReportProblemClick() },
            painterStart = painterResource(id = R.drawable.ic_sup_problem),
            painterEnd = painterResource(id = R.drawable.ic_chevron_right),
            isElevated = false
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        MenuSectionItem(
            title = stringResource(id = R.string.sup_rate_title),
            description = stringResource(id = R.string.sup_rate_desc),
            onClick = { component.onFeedbackClick() },
            painterStart = painterResource(id = R.drawable.ic_sup_rating),
            painterEnd = painterResource(id = R.drawable.ic_chevron_right),
            isElevated = false
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = colorStrokeSeparator
        )

        MenuSectionItem(
            title = stringResource(R.string.support_chat),
            description = stringResource(R.string.support_chat_desc),
            painterStart = painterResource(id = R.drawable.ic_sup_faq),
            painterEnd = painterResource(id = R.drawable.ic_chevron_right),
            isElevated = false,
            onClick = { component.onSupportChatClick() }
        )
    }

}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SubscriptionScreenPreview() {
    SupportScreen(FakeSupportComponent())
}

