package com.runvpn.app.android

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.dialogs.PrivacyDialog
import com.runvpn.app.android.screens.profile.subscription.buysubscription.BalanceRefillScreen
import com.runvpn.app.android.screens.profile.subscription.buysubscription.BuySubscriptionScreen
import com.runvpn.app.android.screens.dns.ChooseDnsServerScreen
import com.runvpn.app.android.screens.main.MainView
import com.runvpn.app.android.screens.profile.auth.AuthScreen
import com.runvpn.app.android.screens.profile.subscription.SubscriptionRootScreen
import com.runvpn.app.android.screens.profile.traffic.TrafficModuleScreen
import com.runvpn.app.android.screens.profile.traffic.logs.TrafficModuleLogsScreen
import com.runvpn.app.android.screens.servers.myservers.MyServersScreen
import com.runvpn.app.android.screens.servers.serveradd.AddServerScreen
import com.runvpn.app.android.screens.settings.UpdateScreen
import com.runvpn.app.android.screens.settings.about.AboutScreen
import com.runvpn.app.android.screens.settings.connection.splittunneling.SplitTunnelingScreen
import com.runvpn.app.android.screens.settings.support.FaqScreen
import com.runvpn.app.android.screens.settings.support.FeedbackScreen
import com.runvpn.app.android.screens.settings.support.ReportProblemScreen
import com.runvpn.app.android.screens.settings.support.SupportBs
import com.runvpn.app.android.screens.settings.support.SupportScreen
import com.runvpn.app.android.screens.welcome.PrivacyScreen
import com.runvpn.app.android.widgets.CommonAlertDialog
import com.runvpn.app.android.widgets.message.MessageContainer
import com.runvpn.app.data.settings.domain.AppTheme
import com.runvpn.app.feature.home.trafficover.ChooseUsageModeComponent
import com.runvpn.app.feature.settings.support.dialog.SupportDialogComponent
import com.runvpn.app.feature.settings.update.UpdateComponent
import com.runvpn.app.presentation.root.RootComponent
import com.runvpn.app.tea.decompose.dialogs.alert.AlertDialogComponent
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@Composable
fun RootView(component: RootComponent, modifier: Modifier = Modifier) {

    // val appTheme by component.appTheme.subscribeAsState() -- here is no dark theme yet
    val dialogState by component.dialog.subscribeAsState()

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            component.onPermissionGrantedResult(
                context.checkSelfPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        component.oneTimeEvent.collectLatest {
            when (it) {
                is RootComponent.OneTimeEvent.InstallUpdate -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = FileProvider.getUriForFile(
                        context,
                        context.packageName + ".update.provider",
                        File(context.cacheDir.toString() + "/update" + "/${it.filePath}")
                    )
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.setDataAndType(uri, "application/vnd.android.package-archive")
                    println(uri.path)
                    context.startActivity(intent)
                }
            }
        }
    }

    RunVpnTheme(appTheme = AppTheme.LIGHT) {
        Scaffold(
            modifier = modifier.fillMaxSize()
        ) { innerPaddings ->
            Children(
                stack = component.childStack,
                animation = stackAnimation(fade()),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings)
            ) { childInstance ->

                when (val child = childInstance.instance) {
                    /** Common Screens*/

                    is RootComponent.Child.Main -> MainView(child.component)

                    is RootComponent.Child.PrivacyPolicy -> PrivacyScreen(component = child.component)


                    /** Screens from Servers Tab*/

                    is RootComponent.Child.ServerAdd -> AddServerScreen(component = child.component)

                    is RootComponent.Child.MyServers -> MyServersScreen(component = child.component)


                    /** Screens from Profile Tab*/

                    is RootComponent.Child.Authorization -> AuthScreen(component = child.component)

                    is RootComponent.Child.SubscriptionRoot -> SubscriptionRootScreen(component = child.component)

                    is RootComponent.Child.TrafficModule -> TrafficModuleScreen(component = child.component)

                    is RootComponent.Child.TrafficModuleLogs -> TrafficModuleLogsScreen(component = child.component)

                    is RootComponent.Child.BalanceRefill -> BalanceRefillScreen(component = child.component)

                    is RootComponent.Child.BuySubscription -> BuySubscriptionScreen(component = child.component)

                    /** Screens from Settings Tab*/

                    is RootComponent.Child.ChooseDnsServer -> ChooseDnsServerScreen(component = child.component)

                    is RootComponent.Child.SplitTunneling -> SplitTunnelingScreen(component = child.component)

                    is RootComponent.Child.SupportMain -> SupportScreen(component = child.component)

                    is RootComponent.Child.Faq -> FaqScreen(component = child.component)

                    is RootComponent.Child.ReportProblem -> ReportProblemScreen(component = child.component)

                    is RootComponent.Child.FeedBack -> FeedbackScreen(component = child.component)

                    is RootComponent.Child.About -> AboutScreen(component = child.component)

                }
            }

            when (val child = dialogState.child?.instance) {
                is UpdateComponent -> {
                    UpdateScreen(child)
                }

                is AlertDialogComponent -> {
                    CommonAlertDialog(
                        dialogMessage = child.dialogMessage,
                        onDismissRequest = { child.onDismissClicked() },
                        onConfirmation = {
                            println("Confirmation registered") // Add logic here to handle confirmation.
                            child.onConfirm()
                        },
                        icon = Icons.Default.Info,
                    )
                }

                is ChooseUsageModeComponent -> {
                    PrivacyDialog(component = child)
                }

                is SupportDialogComponent -> SupportBs(child)

                /** Сюда можно добавить еще один тип диалогового окна об отсутсвии резрешения,
                 *  который открывает системниые настройки приложения*/

            }

            MessageContainer(
                component = component.messageComponent,
                bottomPadding = 16.dp,
                modifier = Modifier.padding(innerPaddings),
            )
        }
    }
}
