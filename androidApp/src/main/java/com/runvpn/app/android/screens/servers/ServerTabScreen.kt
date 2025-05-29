package com.runvpn.app.android.screens.servers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.runvpn.app.android.screens.servers.serverlist.ServerListScreen
import com.runvpn.app.feature.servertab.ServerTabComponent

@Composable
fun ServerTabScreen(component: ServerTabComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.childStack,
        animation = stackAnimation(slide()),
        modifier = modifier
    ) { childInstance ->

        when (val child = childInstance.instance) {
            is ServerTabComponent.Child.ServerList -> ServerListScreen(
                component = child.serverListComponent
            )

        }
    }
}
