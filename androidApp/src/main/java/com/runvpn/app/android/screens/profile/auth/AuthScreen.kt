package com.runvpn.app.android.screens.profile.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.runvpn.app.feature.authorization.AuthorizationComponent

@Composable
fun AuthScreen(
    component: AuthorizationComponent,
    modifier: Modifier = Modifier
) {

    Children(
        stack = component.childStack,
        animation = stackAnimation(slide())
    ) { childInstance ->

        when (val child = childInstance.instance) {
            is AuthorizationComponent.Child.Email -> EmailScreen(
                component = child.emailComponent,
                modifier = modifier
            )

            is AuthorizationComponent.Child.ConfirmCode -> ConfirmCodeScreen(
                component = child.confirmCodeComponent,
                modifier = modifier
            )

            is AuthorizationComponent.Child.EnterPassword -> EnterPasswordScreen(
                component = child.component
            )

            is AuthorizationComponent.Child.SetPassword -> SetPasswordScreen(
                component = child.component
            )
        }
    }
}
