package com.runvpn.app.feature.serveradd

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.feature.serveradd.ikev2.DefaultIkev2ConfigComponent
import com.runvpn.app.feature.serveradd.ikev2.Ikev2ConfigComponent
import com.runvpn.app.feature.serveradd.openvpn.DefaultOpenVpnServerConfigComponent
import com.runvpn.app.feature.serveradd.openvpn.OpenVpnServerConfigComponent
import com.runvpn.app.feature.serveradd.oversocks.DefaultOverSocksConfigComponent
import com.runvpn.app.feature.serveradd.oversocks.OverSocksConfigComponent
import com.runvpn.app.feature.serveradd.wireguard.DefaultWireGuardConfigComponent
import com.runvpn.app.feature.serveradd.wireguard.WireGuardConfigComponent
import com.runvpn.app.feature.serveradd.xray.DefaultXrayVpnServerConfigComponent
import com.runvpn.app.feature.serveradd.xray.XrayVpnServerConfigComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get


fun DecomposeComponentFactory.createServerAddComponent(
    componentContext: ComponentContext,
    serverToEdit: String?,
    output: (AddServerComponent.Output) -> Unit
): AddServerComponent {
    return DefaultAddServerComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        serverToEdit,
        output = output
    )
}

fun DecomposeComponentFactory.createOpenVpnAddServerComponent(
    componentContext: ComponentContext,
    serverToEdit: Server?,
    output: (OpenVpnServerConfigComponent.Output) -> Unit
): OpenVpnServerConfigComponent {
    return DefaultOpenVpnServerConfigComponent(
        componentContext,
        get(),
        get(),
        get(),
        serverToEdit = serverToEdit,
        output = output
    )
}


fun DecomposeComponentFactory.createXrayServerConfigComponent(
    componentContext: ComponentContext,
    serverToEdit: Server?,
    output: (XrayVpnServerConfigComponent.Output) -> Unit
): XrayVpnServerConfigComponent {
    return DefaultXrayVpnServerConfigComponent(
        componentContext,
        get(),
        get(),
        get(),
        serverToEdit = serverToEdit,
        output = output
    )
}


fun DecomposeComponentFactory.createWireGuardConfigComponent(
    componentContext: ComponentContext,
    serverToEdit: Server?,
    output: (WireGuardConfigComponent.Output) -> Unit
): WireGuardConfigComponent {
    return DefaultWireGuardConfigComponent(
        componentContext,
        get(),
        get(),
        serverToEdit = serverToEdit,
        output = output
    )
}

fun DecomposeComponentFactory.createOverSocksConfigComponent(
    componentContext: ComponentContext,
    serverToEdit: Server?,
    output: (OverSocksConfigComponent.Output) -> Unit
): OverSocksConfigComponent {
    return DefaultOverSocksConfigComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        serverToEdit,
        output
    )
}


fun DecomposeComponentFactory.createIkev2ConfigComponent(
    componentContext: ComponentContext,
    serverToEdit: Server?,
    output: (Ikev2ConfigComponent.Output) -> Unit
): Ikev2ConfigComponent {
    return DefaultIkev2ConfigComponent(
        componentContext,
        get(),
        serverToEdit,
        output
    )
}

