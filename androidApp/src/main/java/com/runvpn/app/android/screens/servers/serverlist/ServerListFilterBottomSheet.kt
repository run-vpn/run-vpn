package com.runvpn.app.android.screens.servers.serverlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.widgets.ItemListBottomSheet
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.feature.serverlist.ServerListComponent


@Composable
fun ServerListFilterBs(
    onFilterSelected: (ServerListComponent.ServerListFilter) -> Unit,
    onDismiss: () -> Unit
) {

    ItemListBottomSheet(
        title = stringResource(id = R.string.servers_filter),
        items = ServerListComponent.ServerListFilter.entries,
        onItemClick = { onFilterSelected(it) },
        onDismiss = onDismiss
    ) {
        FilterItem(filter = it)
    }
}


@Composable
private fun FilterItem(
    filter: ServerListComponent.ServerListFilter
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = filter.toImageResource()),
            contentDescription = "",
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = filter.toLocalizedString(context),
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stringResource(id = filter.getDescriptionString()),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color(0xFF797A80)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "",
            tint = hintTextColor
        )
    }
}


private fun ServerListComponent.ServerListFilter.getDescriptionString(): Int = when (this) {
    ServerListComponent.ServerListFilter.ALL -> R.string.all_servers_desc
    ServerListComponent.ServerListFilter.RUN_SERVICE -> R.string.run_servers_desc
    ServerListComponent.ServerListFilter.RUN_CLIENT -> R.string.servers_run_clients_desc
    ServerListComponent.ServerListFilter.CUSTOM -> R.string.your_servers
}


fun ServerListComponent.ServerListFilter.toImageResource(): Int = when (this) {
    ServerListComponent.ServerListFilter.ALL -> R.drawable.ic_all_servers
    ServerListComponent.ServerListFilter.RUN_SERVICE -> R.drawable.ic_run_server
    ServerListComponent.ServerListFilter.RUN_CLIENT -> R.drawable.ic_run_server
    ServerListComponent.ServerListFilter.CUSTOM -> R.drawable.ic_your_servers
}

@Preview
@Composable
private fun ServerListBsPreview() {
    RunVpnTheme {
        ServerListFilterBs(
            onFilterSelected = { },
            onDismiss = {}
        )
    }
}
