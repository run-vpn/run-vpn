package com.runvpn.app.android.screens.dns

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.android.widgets.CommonAlertDialog
import com.runvpn.app.android.widgets.SwipeToDeleteContainer
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.textGrayColor
import com.runvpn.app.core.ui.textSecondaryColor
import com.runvpn.app.data.connection.domain.DnsServer
import com.runvpn.app.feature.settings.dns.ChooseDnsServerComponent
import com.runvpn.app.feature.settings.dns.FakeChooseDnsServerComponent
import com.runvpn.app.feature.settings.dns.adddialog.CreateDnsServerDialogComponent
import com.runvpn.app.tea.decompose.dialogs.alert.AlertDialogComponent


@Composable
fun ChooseDnsServerScreen(
    component: ChooseDnsServerComponent
) {
    val context = LocalContext.current

    val state by component.state.subscribeAsState()

    val childSlot by component.childSlot.subscribeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppToolBar(
            onBackClick = { dispatchOnBackPressed(context) },
            title = stringResource(id = R.string.settings_dns_title),
            subtitle = stringResource(id = R.string.settings_dns_desc)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
        ) {
            item {
                Text(
                    text = stringResource(R.string.reconnect_to_apply_changes_hint),
                    color = colorIconAccent,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .background(lightBlue, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.popular_dns_servers),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textBlack,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )
            }
            itemsIndexed(state.defaultServers) { index, item ->
                DnsServerItem(
                    server = item,
                    isSelected = item == state.selectedServer,
                    onClick = { component.onChooseDnsServerClick(item) }
                )

                if (index != state.defaultServers.size - 1) {
                    HorizontalDivider(color = colorStrokeSeparator)
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.your_dns_servers),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textBlack,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        bottom = 8.dp
                    )
                )
            }
            if (state.servers.isNotEmpty()) {
                itemsIndexed(state.servers, key = { _, item -> item.id }) { index, item ->
                    SwipeToDeleteContainer(
                        item = item,
                        onDelete = component::onDeleteDnsServerClick,
                        content = { server ->
                            DnsServerItem(
                                server = server,
                                isSelected = item == state.selectedServer,
                                onClick = { component.onChooseDnsServerClick(item) }
                            )
                        }
                    )

                    if (index != state.servers.size - 1) {
                        HorizontalDivider(color = colorStrokeSeparator)
                    }
                }
            } else {
                item {
                    Text(
                        text = stringResource(id = R.string.settings_dns_empty_list),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.weight(1f))

                AppButton(
                    onClick = component::onAddServerClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.add))
                }
            }
        }
    }

    when (val child = childSlot.child?.instance) {
        is CreateDnsServerDialogComponent -> AddDnsServerDialog(component = child)

        is AlertDialogComponent -> {
            CommonAlertDialog(
                dialogMessage = child.dialogMessage,
                onDismissRequest = { child.onDismissClicked() },
                onConfirmation = {
                    child.onConfirm()
                },
                icon = Icons.Default.Info,
            )
        }
    }
}


@Composable
private fun DnsServerItem(
    server: DnsServer,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.White)
            .selectable(
                isSelected,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = true,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(vertical = 4.dp)
            .padding(end = 16.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            interactionSource = interactionSource
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = server.name,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
            Text(
                text = server.primaryIp,
                color = textSecondaryColor,
                fontSize = 12.sp
            )
        }

        Image(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "",
            colorFilter = ColorFilter.tint(textGrayColor)
        )
    }
}

@Preview(showSystemUi = true, backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun ChooseDnsServerPreview() {
    RunVpnTheme {
        ChooseDnsServerScreen(
            component = FakeChooseDnsServerComponent(
                isEmptyMyServerList = true
            )
        )
    }
}
