package com.runvpn.app.android.screens.settings.support

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.feature.settings.support.faq.FakeFaqComponent
import com.runvpn.app.feature.settings.support.faq.FaqComponent

@Composable
fun FaqScreen(component: FaqComponent, modifier: Modifier = Modifier) {

    val state by component.state.subscribeAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

        AppToolBar(
            onBackClick = { component.onBack() },
            iconBack = painterResource(id = R.drawable.ic_arrow_back),
            title = stringResource(id = R.string.sup_faq_title),
            subtitle = stringResource(
                id = R.string.sup_faq_desc
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(state.questions) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_faq_bonuse),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = it,
                    )
                }
                Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Preview(showSystemUi = false, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun FaqScreenPreview() {
    FaqScreen(component = FakeFaqComponent())
}
