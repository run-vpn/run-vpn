//
//  VpnConnectionFactory.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import shared


class DefaultVpnConnectionFactory: VpnConnectionFactory {
    
    func createIkev2Connection(ikev2Config: Ikev2Config) -> any VpnConnection {
        return XrayVpnConnection()
    }
    
    func createOpenVpnConnection(openVpnConfig: OpenVpnConfig) -> any VpnConnection {
        return XrayVpnConnection()
    }
    
    func createOverSocksConnection(overSocksConfig: OverSocksConfig) -> any VpnConnection {
        return XrayVpnConnection()
    }
    
    func createWireGuardConnection(wireGuardConfig: WireGuardConfig) -> any VpnConnection {
        return XrayVpnConnection()
    }
    
    func createXrayVpnConnection(xrayVpnConfig: XrayVpnConfig) -> any VpnConnection {
        return XrayVpnConnection()
    }
}
