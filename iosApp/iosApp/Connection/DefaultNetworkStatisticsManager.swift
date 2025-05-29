//
//  DefaultNetworkStatisticsManager.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class DefaultNetworkStatisticsManager: NetworkStatisticsManager {
    var stats: Value<TeaNullWrapper<NetworkStatisticsManagerNetworkStats>>
    
    init() {
        stats = mutableValue(TeaNullWrapper(wrappedValue: nil))
    }
    
    func startListen() {}
    
    func stopListen() {}
}
