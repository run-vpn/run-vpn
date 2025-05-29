package com.runvpn.app.feature.home

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.data.connection.domain.ConnectionStatistics
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.feature.home.rating.ConnectionReviewComponent
import com.runvpn.app.feature.home.rating.DefaultConnectionReviewComponent
import com.runvpn.app.feature.home.sharewithfriends.DefaultShareWithFriendsComponent
import com.runvpn.app.feature.home.sharewithfriends.ShareWithFriendsComponent
import com.runvpn.app.feature.home.trafficover.ChooseUsageModeComponent
import com.runvpn.app.feature.home.trafficover.DefaultChooseUsageModeComponent
import com.runvpn.app.feature.home.welcome.DefaultWelcomeComponent
import com.runvpn.app.feature.home.welcome.WelcomeComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get

fun DecomposeComponentFactory.createHomeComponent(
    componentContext: ComponentContext,
    onOutput: (HomeComponent.Output) -> Unit
): DefaultHomeComponent {
    return DefaultHomeComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        onOutput
    )
}

fun DecomposeComponentFactory.createConnectionReviewComponent(
    componentContext: ComponentContext,
    connectionStats: ConnectionStatistics,
    server: Server,
    onDismissed: () -> Unit
): ConnectionReviewComponent {
    return DefaultConnectionReviewComponent(
        componentContext,
        get(),
        get(),
        get(),
        connectionStats,
        server,
        onDismissed
    )
}

fun DecomposeComponentFactory.createShareWithFriendsComponent(
    componentContext: ComponentContext,
    output: (ShareWithFriendsComponent.Output) -> Unit
): ShareWithFriendsComponent {
    return DefaultShareWithFriendsComponent(componentContext, output)
}


fun DecomposeComponentFactory.createWelcomeComponent(
    componentContext: ComponentContext,
    hasBackStack: Boolean,
    output: (WelcomeComponent.Output) -> Unit
): WelcomeComponent {
    return DefaultWelcomeComponent(componentContext, get(), get(), hasBackStack, output)
}


fun DecomposeComponentFactory.createTrafficOverComponent(
    componentContext: ComponentContext,
    isCancellable: Boolean,
    onDismiss: () -> Unit
): ChooseUsageModeComponent {
    return DefaultChooseUsageModeComponent(componentContext, get(), isCancellable, onDismiss)
}
