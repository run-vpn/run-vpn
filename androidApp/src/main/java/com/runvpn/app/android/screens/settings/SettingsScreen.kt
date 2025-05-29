package com.runvpn.app.android.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.runvpn.app.android.screens.settings.about.AboutScreen
import com.runvpn.app.android.screens.settings.common.CommonSettingsScreen
import com.runvpn.app.android.screens.settings.connection.ConnectionSettingsScreen
import com.runvpn.app.android.screens.settings.main.MainSettingsScreen
import com.runvpn.app.android.screens.settings.support.SupportScreen
import com.runvpn.app.feature.settings.SettingsComponent

@Composable
fun SettingsScreen(component: SettingsComponent, modifier: Modifier = Modifier) {
    Children(stack = component.childStack, animation = stackAnimation(fade())) {
        when (val child = it.instance) {
            is SettingsComponent.Child.Main -> MainSettingsScreen(
                component = child.component,
                modifier = modifier
            )

            is SettingsComponent.Child.Common -> CommonSettingsScreen(
                component = child.component,
                modifier = modifier
            )

            is SettingsComponent.Child.Connection -> ConnectionSettingsScreen(
                component = child.component,
                modifier = modifier
            )

            is SettingsComponent.Child.Support -> SupportScreen(
                component = child.component,
                modifier = modifier
            )

            is SettingsComponent.Child.AboutApp -> AboutScreen(
                component = child.component,
                modifier = modifier
            )
        }
    }
}
