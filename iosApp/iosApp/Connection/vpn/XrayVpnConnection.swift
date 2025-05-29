//
//  XrayConnectionFactory.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import shared

class XrayVpnConnection: VpnConnection {
    let connectionManager: VpnConnectionManager
    
    init() {
        self.connectionManager = SharedSDK.shared.vpnConnectionManager
    }
    
    func connect() {
        self.connectionManager.setConnectionStatus(newStatus: ConnectionStatusConnected())
    }
    
    func disconnect() {
        self.connectionManager.setConnectionStatus(newStatus: ConnectionStatusDisconnected())
    }
    
    func pause() {
        self.connectionManager.setConnectionStatus(newStatus: ConnectionStatusPaused())
    }
}
