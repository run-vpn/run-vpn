package com.runvpn.app.android.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.toLocaleString
import com.runvpn.app.android.widgets.LoadingShimmerView
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.entities.SuggestedServers
import com.runvpn.app.data.servers.utils.TestData
import com.runvpn.app.data.settings.domain.SuggestedServersMode

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ServersInfoView(
    isLoading: Boolean,
    suggestedServers: SuggestedServers?,
    onSuggestedServerClick: (Server) -> Unit,
    modifier: Modifier = Modifier
) {

    var loadStatus = remember {
        isLoading
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {

        if (suggestedServers?.servers == null) {
            Text(
                text = "",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .alpha(0.6f)
                    .padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LoadingShimmerView(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )
                LoadingShimmerView(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )
                LoadingShimmerView(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )
            }
        } else {
//            if (suggestedServers.servers.isNotEmpty()) {
            Text(
                text = suggestedServers?.mode?.toLocaleString(LocalContext.current) ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .alpha(0.6f)
                    .padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(suggestedServers?.servers ?: listOf(), key = { it.uuid }) {
                    FavouriteServerItem(
                        server = it,
                        onServerClicked = { server -> onSuggestedServerClick(server) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
//            }
        }
    }
}

@Preview(name = "Loading", showBackground = true, backgroundColor = 0xFFEFF2F5)
@Composable
fun ServersInfoViewPreview() {
    RunVpnTheme {
        ServersInfoView(
            isLoading = true,
            suggestedServers = SuggestedServers(SuggestedServersMode.FAVORITES, listOf()),
            onSuggestedServerClick = {}
        )
    }
}

@Preview(name = "Loaded", showBackground = true, backgroundColor = 0xFFEFF2F5)
@Composable
fun ServersInfoViewLoadedPreview() {
    RunVpnTheme {
        ServersInfoView(
            isLoading = false,
            suggestedServers = SuggestedServers(
                SuggestedServersMode.FAVORITES,
                TestData.testServerList
            ),
            onSuggestedServerClick = {}
        )
    }
}
