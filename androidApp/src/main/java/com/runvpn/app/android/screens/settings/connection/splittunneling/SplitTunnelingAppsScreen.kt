package com.runvpn.app.android.screens.settings.connection.splittunneling

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberAsyncImagePainter
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.android.widgets.CommonAlertDialog
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.colorRadioButtonUnchecked
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.data.settings.domain.SplitTunnelingMode
import com.runvpn.app.feature.settings.splittunneling.apps.FakeSplitTunnelingAppsComponent
import com.runvpn.app.feature.settings.splittunneling.apps.SplitTunnelingAppsComponent
import com.runvpn.app.tea.decompose.dialogs.alert.AlertDialogComponent


@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("QueryPermissionsNeeded", "CoroutineCreationDuringComposition")
@Composable
fun SplitTunnelingAppsScreen(
    component: SplitTunnelingAppsComponent,
    modifier: Modifier = Modifier
) {
    val dialogState by component.dialog.subscribeAsState()

    val context = LocalContext.current
    val state by component.state.subscribeAsState()

    val radioOptions = listOf(
        SplitTunnelingMode.ALL,
        SplitTunnelingMode.EXCLUDE,
        SplitTunnelingMode.INCLUDE
    )

    Column {
        AppToolBar(
            onBackClick = component::onBackClick,
            title = stringResource(id = R.string.excluded_applications),
            subtitle = stringResource(id = R.string.excluded_applications)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = modifier.fillMaxWidth()
        ) {
            item(key = "title") {
                Column(modifier = Modifier.fillMaxWidth()) {

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

                    SectionHeader(title = stringResource(R.string.when_vpn_connected))

                    radioOptions.forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    component.onChangeSplitMode(mode)
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                        ) {
                            RadioButton(
                                selected = (mode == state.splitMode),
                                onClick = {
                                    component.onChangeSplitMode(mode)
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colorIconAccent,
                                    unselectedColor = colorRadioButtonUnchecked
                                )
                            )

                            Text(
                                text = stringResource(id = getModeLocaleString(mode)),
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }
                    }

                    SectionHeader(title = stringResource(R.string.selected_applications))

                }
            }


            items(state.excludedApps, key = { item -> item.packageName }) {

                val icon = rememberAsyncImagePainter(
                    model = try {
                        context.packageManager.getApplicationIcon(it.packageName)
                            .toBitmap(config = Bitmap.Config.ARGB_8888)

                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        ContextCompat.getDrawable(context, R.drawable.ic_tab_settings)!!
                            .toBitmap(config = Bitmap.Config.ARGB_8888)
                    }
                )

                SplitTunnelingApplicationItem(
                    application = it,
                    icon = icon,
                    modifier = Modifier.animateItemPlacement(),
                    onClick = component::onRemoveApp,
                    actionIcon = painterResource(id = R.drawable.ic_remove_from_split_tunneling)
                )
            }

            item(key = "divider") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    SectionHeader(title = stringResource(R.string.installed_applications))

                    if (state.loading) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(state.allApps, key = { item -> item.packageName }) {
                val icon = rememberAsyncImagePainter(
                    model = try {
                        context.packageManager.getApplicationIcon(it.packageName)
                            .toBitmap(config = Bitmap.Config.ARGB_8888)
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        ContextCompat.getDrawable(context, R.drawable.ic_tab_settings)!!
                            .toBitmap(config = Bitmap.Config.ARGB_8888)
                    }
                )
                SplitTunnelingApplicationItem(
                    application = it,
                    icon = icon,
                    modifier = Modifier.animateItemPlacement(),
                    onClick = component::onAddApp
                )
            }
        }
    }

    when (val child = dialogState.child?.instance) {
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
fun SectionHeader(modifier: Modifier = Modifier, title: String) {

    Spacer(modifier = modifier.height(20.dp))

    Text(
        text = title,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))
}

private fun getModeLocaleString(mode: SplitTunnelingMode) = when (mode) {
    SplitTunnelingMode.ALL -> R.string.all_apps_using_vpn
    SplitTunnelingMode.EXCLUDE -> R.string.exclude_selected_apps
    SplitTunnelingMode.INCLUDE -> R.string.only_selected_apps_using_vpn
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SplitTunnelingAppsScreenPreview() {
    SplitTunnelingAppsScreen(component = FakeSplitTunnelingAppsComponent())
}
