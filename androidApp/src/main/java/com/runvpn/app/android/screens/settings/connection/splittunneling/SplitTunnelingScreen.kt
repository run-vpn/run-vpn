package com.runvpn.app.android.screens.settings.connection.splittunneling

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.runvpn.app.feature.settings.splittunneling.SplitTunnelingComponent

@Composable
fun SplitTunnelingScreen(component: SplitTunnelingComponent) {


    Children(stack = component.childStack) { childInstance ->

        when (val child = childInstance.instance) {
            is SplitTunnelingComponent.Child.Main -> SplitTunnelingMainScreen(
                component = child.component,
            )

            is SplitTunnelingComponent.Child.Apps -> SplitTunnelingAppsScreen(
                component = child.component,
            )

            is SplitTunnelingComponent.Child.Ips -> SplitTunnelingIpsScreen(
                component = child.component,
            )

        }
    }


}
