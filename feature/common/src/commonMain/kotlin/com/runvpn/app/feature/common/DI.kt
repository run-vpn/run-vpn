package com.runvpn.app.feature.common

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.feature.common.browser.BrowserComponent
import com.runvpn.app.feature.common.browser.DefaultBrowserComponent
import com.runvpn.app.feature.common.dialogs.connectionerror.ConnectionErrorComponent
import com.runvpn.app.feature.common.dialogs.connectionerror.DefaultConnectionErrorComponent
import com.runvpn.app.feature.common.dialogs.selectfromlist.DefaultSelectFromListComponent
import com.runvpn.app.feature.common.dialogs.selectfromlist.SelectFromListComponent
import com.runvpn.app.feature.common.dialogs.shareqrcode.DefaultShareQrCodeComponent
import com.runvpn.app.feature.common.dialogs.shareqrcode.ShareQrCodeComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get

fun DecomposeComponentFactory.createBrowserComponent(
    componentContext: ComponentContext,
    url: String
): BrowserComponent {
    return DefaultBrowserComponent(componentContext, url)
}


fun DecomposeComponentFactory.createShareQrCodeComponent(
    componentContext: ComponentContext,
    link: String,
    onDismiss: () -> Unit
): ShareQrCodeComponent {
    return DefaultShareQrCodeComponent(
        componentContext,
        link,
        onDismiss
    )
}

fun <T> DecomposeComponentFactory.createSelectFromListComponent(
    componentContext: ComponentContext,
    title: String,
    items: List<T>,
    onItemSelected: (Int) -> Unit,
    onDismiss: () -> Unit
): SelectFromListComponent<T> {
    return DefaultSelectFromListComponent(
        componentContext = componentContext,
        title = title,
        items = items,
        onItemSelected = onItemSelected,
        onDismiss = onDismiss
    )
}

fun DecomposeComponentFactory.createConnectionErrorComponent(
    componentContext: ComponentContext,
    onDismiss: () -> Unit
): ConnectionErrorComponent {
    return DefaultConnectionErrorComponent(componentContext, get(), onDismiss)
}
