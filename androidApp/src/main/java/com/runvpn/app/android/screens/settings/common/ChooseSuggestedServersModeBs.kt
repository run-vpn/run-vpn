package com.runvpn.app.android.screens.settings.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.toLocaleString
import com.runvpn.app.android.widgets.ItemListBottomSheet
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.feature.settings.suggestedserversmode.ChooseSuggestedServersModeComponent
import com.runvpn.app.feature.settings.suggestedserversmode.FakeChooseSuggestedServersModeComponent

@Composable
fun ChooseSuggestedServersModeBs(
    component: ChooseSuggestedServersModeComponent,
    modifier: Modifier = Modifier
) {

    val allModes by component.allModes.subscribeAsState()
    val currentMode by component.currentMode.subscribeAsState()

    ItemListBottomSheet(
        items = allModes,
        title = stringResource(R.string.choose_suggested_server_mode),
        onDismiss = component::onDismissClicked,
        onItemClick = component::onCurrentModeChoose,
        spacerBottomHeight = 20.dp,
        modifier = modifier
    ) {
        SuggestedServerModeItem(
            mode = it,
            isSelected = it == currentMode.wrappedValue,
        )
    }
}


@Composable
fun SuggestedServerModeItem(
    mode: SuggestedServersMode,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(50.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        RoundedImage(
            painter = painterResource(
                id = getItemIcon(mode)
            ),
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
                .background(lightBlue, RoundedCornerShape(100f))
                .padding(6.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = mode.toLocaleString(LocalContext.current),
            fontSize = 16.sp,
            fontWeight = FontWeight(400),
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Spacer(modifier = Modifier.width(12.dp))
            Image(painter = painterResource(id = R.drawable.ic_done), contentDescription = "")
        }
    }
}

private fun getItemIcon(mode: SuggestedServersMode) = when (mode) {
    SuggestedServersMode.NONE -> R.drawable.ic_hidden
    SuggestedServersMode.AUTO -> R.drawable.ic_lamp
    SuggestedServersMode.FAVORITES -> R.drawable.ic_star_16
    SuggestedServersMode.RECENT -> R.drawable.ic_replay
    SuggestedServersMode.RECOMMENDED -> R.drawable.ic_stars
}

@Preview
@Composable
private fun ChooseSuggestedServerModeBsPreview() {
    ChooseSuggestedServersModeBs(component = FakeChooseSuggestedServersModeComponent())
}
