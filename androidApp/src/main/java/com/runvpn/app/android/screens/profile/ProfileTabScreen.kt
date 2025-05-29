package com.runvpn.app.android.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.runvpn.app.android.screens.profile.main.ProfileMainScreen
import com.runvpn.app.feature.profile.ProfileTabComponent

@Composable
fun ProfileTabScreen(component: ProfileTabComponent, modifier: Modifier = Modifier) {
    Children(stack = component.childStack, animation = stackAnimation(fade())) { childInstance ->

        when (val child = childInstance.instance) {
            is ProfileTabComponent.Child.Main -> ProfileMainScreen(
                component = child.profileComponent,
                modifier = modifier
            )
        }
    }
}

