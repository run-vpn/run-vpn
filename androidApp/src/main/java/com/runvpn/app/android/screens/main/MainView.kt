package com.runvpn.app.android.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.BuildConfig
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.findActivity
import com.runvpn.app.android.screens.home.HomeScreen
import com.runvpn.app.android.screens.profile.ProfileTabScreen
import com.runvpn.app.android.screens.servers.ServerTabScreen
import com.runvpn.app.android.screens.settings.SettingsScreen
import com.runvpn.app.android.widgets.BottomNavigationBar
import com.runvpn.app.android.widgets.NavBarItem
import com.runvpn.app.android.widgets.message.noOverlapByMessage
import com.runvpn.app.core.ui.backgroundAccentColor
import com.runvpn.app.presentation.main.MainComponent


@Composable
fun MainView(component: MainComponent, modifier: Modifier = Modifier) {

    val childStack by component.activeChild.subscribeAsState()
    val state by component.state.subscribeAsState()

    val context = LocalContext.current

    val isProfileEnabled by remember {
        mutableStateOf(BuildConfig.FLAVOR != "noproxyGp")
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                modifier = Modifier.noOverlapByMessage()
            ) {
                NavBarItem(
                    selected = childStack.active.instance is MainComponent.Child.Home,
                    onClick = { component.onHomeClicked() },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tab_home),
                            contentDescription = null
                        )
                    },
                    label = stringResource(id = R.string.tab_home)
                )
                NavBarItem(
                    selected = childStack.active.instance is MainComponent.Child.Servers,
                    onClick = { component.onServersClicked() },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tab_servers),
                            contentDescription = null
                        )
                    },
                    label = stringResource(id = R.string.tab_servers)
                )
//                NavBarItem(
//                    selected = childStack.active.instance is MainComponent.Child.Profile,
//                    onClick = {
//                        component.onProfileClicked()
//                    },
//                    disabledAction = {
//                        Toast.makeText(context, R.string.not_implemented, Toast.LENGTH_SHORT)
//                            .show()
//                    },
//                    enabled = isProfileEnabled,
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_tab_profile),
//                            contentDescription = null
//                        )
//                    },
//                    label = stringResource(id = R.string.tab_profile)
//                )
                NavBarItem(
                    selected = childStack.active.instance is MainComponent.Child.Settings,
                    onClick = { component.onSettingsClicked() },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tab_settings),
                            contentDescription = null
                        )
                    },
                    hasBadge = state.hasUpdate,
                    label = stringResource(id = R.string.tab_settings)
                )
            }
        },
        contentWindowInsets = WindowInsets.navigationBars,
        modifier = modifier
    ) { innerPadding ->
        Children(
            stack = component.activeChild,
            modifier = Modifier.padding(innerPadding)
        ) { childInstance ->
            when (val child = childInstance.instance) {
                is MainComponent.Child.Home -> {
                    SideEffect {
                        val window = context.findActivity()?.window
                        window?.statusBarColor = backgroundAccentColor.toArgb()
                    }

                    HomeScreen(
                        component = child.component,
                    )
                }

                is MainComponent.Child.Profile -> {
                    SideEffect {
                        val window = context.findActivity()?.window
                        window?.statusBarColor = Color.White.toArgb()
                    }

                    ProfileTabScreen(
                        component = child.component,
                    )
                }

                is MainComponent.Child.Servers -> {
                    SideEffect {
                        val window = context.findActivity()?.window
                        window?.statusBarColor = Color.White.toArgb()
                    }

                    ServerTabScreen(
                        component = child.component,
                    )
                }

                is MainComponent.Child.Settings -> {
                    SideEffect {
                        val window = context.findActivity()?.window
                        window?.statusBarColor = Color.White.toArgb()
                    }

                    SettingsScreen(
                        component = child.component,
                    )
                }
            }
        }
    }
}

