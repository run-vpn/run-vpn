package com.runvpn.app.feature.settings

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.data.settings.domain.Language
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.feature.settings.common.CommonSettingsComponent
import com.runvpn.app.feature.settings.common.DefaultCommonSettingsComponent
import com.runvpn.app.feature.settings.connection.ConnectionSettingsComponent
import com.runvpn.app.feature.settings.connection.DefaultConnectionSettingsComponent
import com.runvpn.app.feature.settings.dns.ChooseDnsServerComponent
import com.runvpn.app.feature.settings.dns.DefaultChooseDnsServerComponent
import com.runvpn.app.feature.settings.language.ChooseLanguageComponent
import com.runvpn.app.feature.settings.language.DefaultChooseLanguageComponent
import com.runvpn.app.feature.settings.main.DefaultMainSettingsComponent
import com.runvpn.app.feature.settings.main.MainSettingsComponent
import com.runvpn.app.feature.settings.splittunneling.DefaultSplitTunnelingComponent
import com.runvpn.app.feature.settings.splittunneling.SplitTunnelingComponent
import com.runvpn.app.feature.settings.splittunneling.apps.DefaultSplitTunnelingAppsComponent
import com.runvpn.app.feature.settings.splittunneling.apps.SplitTunnelingAppsComponent
import com.runvpn.app.feature.settings.splittunneling.ips.DefaultSplitTunnelingIpsComponent
import com.runvpn.app.feature.settings.splittunneling.ips.SplitTunnelingIpsComponent
import com.runvpn.app.feature.settings.splittunneling.main.DefaultSplitTunnelingMainComponent
import com.runvpn.app.feature.settings.splittunneling.main.SplitTunnelingMainComponent
import com.runvpn.app.feature.settings.suggestedserversmode.ChooseSuggestedServersModeComponent
import com.runvpn.app.feature.settings.suggestedserversmode.DefaultChooseSuggestedServersModeComponent
import com.runvpn.app.feature.settings.support.about.AboutComponent
import com.runvpn.app.feature.settings.support.about.DefaultAboutComponent
import com.runvpn.app.feature.settings.support.dialog.DefaultSupportDialogComponent
import com.runvpn.app.feature.settings.support.dialog.SupportDialogComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get

fun DecomposeComponentFactory.createSettingsComponent(
    componentContext: ComponentContext
): SettingsComponent {
    return DefaultSettingsComponent(componentContext, get(), get())
}

fun DecomposeComponentFactory.createMainSettingsComponent(
    componentContext: ComponentContext,
    onOutput: (MainSettingsComponent.Output) -> Unit
): MainSettingsComponent {
    return DefaultMainSettingsComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        onOutput
    )
}

fun DecomposeComponentFactory.createCommonSettingsComponent(
    componentContext: ComponentContext
): CommonSettingsComponent {
    return DefaultCommonSettingsComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        get()
    )
}

fun DecomposeComponentFactory.createConnectionSettingsComponent(
    componentContext: ComponentContext,
    output: (ConnectionSettingsComponent.Output) -> Unit
): ConnectionSettingsComponent {
    return DefaultConnectionSettingsComponent(componentContext, get(), get(), get(), output)
}


fun DecomposeComponentFactory.createChooseLanguageComponent(
    componentContext: ComponentContext,
    onLanguageSelected: (Language) -> Unit,
    onDismissed: () -> Unit
): ChooseLanguageComponent {
    return DefaultChooseLanguageComponent(componentContext, get(), onLanguageSelected, onDismissed)
}


fun DecomposeComponentFactory.createSupportComponent(
    componentContext: ComponentContext,
    onDismissed: () -> Unit
): SupportDialogComponent {
    return DefaultSupportDialogComponent(componentContext, get(), get(), get(), onDismissed)
}

fun DecomposeComponentFactory.createChooseSuggestedMode(
    componentContext: ComponentContext,
    onSuggestedModeChoose: (SuggestedServersMode) -> Unit,
    onDismissed: () -> Unit
): ChooseSuggestedServersModeComponent {
    return DefaultChooseSuggestedServersModeComponent(
        componentContext,
        get(),
        get(),
        onSuggestedModeChoose,
        onDismissed
    )
}

fun DecomposeComponentFactory.createAboutComponent(
    componentContext: ComponentContext,
    output: (AboutComponent.Output) -> Unit
): AboutComponent {
    return DefaultAboutComponent(componentContext, get(), get(), get(), get(), output)
}

fun DecomposeComponentFactory.createSplitTunnelingComponent(
    componentContext: ComponentContext
): SplitTunnelingComponent {
    return DefaultSplitTunnelingComponent(componentContext, get(), get())
}

fun DecomposeComponentFactory.createSplitTunnelingMainComponent(
    componentContext: ComponentContext,
    output: (SplitTunnelingMainComponent.Output) -> Unit
): SplitTunnelingMainComponent {
    return DefaultSplitTunnelingMainComponent(componentContext, get(), get(), output)
}

fun DecomposeComponentFactory.createSplitTunnelingAppsComponent(
    componentContext: ComponentContext,
    output: (SplitTunnelingAppsComponent.Output) -> Unit
): SplitTunnelingAppsComponent {
    return DefaultSplitTunnelingAppsComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        output
    )
}

fun DecomposeComponentFactory.createSplitTunnelingIpsComponent(
    componentContext: ComponentContext,
    output: (SplitTunnelingIpsComponent.Output) -> Unit
): SplitTunnelingIpsComponent {
    return DefaultSplitTunnelingIpsComponent(componentContext, output)
}

fun DecomposeComponentFactory.createChooseDnsServerComponent(
    componentContext: ComponentContext
): ChooseDnsServerComponent {
    return DefaultChooseDnsServerComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get()
    )
}
