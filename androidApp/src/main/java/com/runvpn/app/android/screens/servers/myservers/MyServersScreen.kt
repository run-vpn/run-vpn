package com.runvpn.app.android.screens.servers.myservers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.feature.myservers.FakeMyServersComponent
import com.runvpn.app.feature.myservers.MyServersComponent


@Composable
fun MyServersScreen(component: MyServersComponent, modifier: Modifier = Modifier) {

    val state by component.state.subscribeAsState()

    Box(modifier = modifier.fillMaxSize()) {

        if (state.servers.isEmpty()) {
            Text(
                text = stringResource(R.string.hint_no_imported_servers),
                fontSize = 16.sp,
                color = hintTextColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = { component.onBackClick() },
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        tint = Color.Unspecified,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(id = R.string.my_servers),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.my_servers_desc),
                        fontSize = 14.sp,
                        color = hintTextColor,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn {
                items(state.servers, key = { it.uuid }) {
                    MyServerItem(
                        modifier = Modifier.fillMaxWidth(),
                        isCurrentServer = false,
                        customServer = it,
                        onEditClick = component::onEditServerClick,
                        onDeleteClick = component::onDeleteServerClick
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        AppButton(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            onClick = component::onAddServerClick
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = null)
            Text(text = stringResource(R.string.add_new_server))
        }
    }

}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun MyServersScreenPreview() {
    MyServersScreen(component = FakeMyServersComponent())
}
