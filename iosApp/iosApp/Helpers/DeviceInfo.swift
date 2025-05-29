//
//  DeviceInfo.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

func createDeviceInfo() -> DeviceInfo {
    return DeviceInfo(
        guid: "1234",
        timezone: "utc", 
        language: "ru",
        pushToken: "some",
        source: "store",
        referrer: nil,
        hardware: createHardware(),
        software: createSoftware(),
        application: createApplicationInfo(),
        customData: nil,
        applicationPackageName: "com.runvpn.app"
    )
}

private func createApplicationInfo() -> ApplicationInfo {
    return ApplicationInfo(code: "com.runvpn.app", source: "store")
}

private func createHardware() -> Hardware_ {
    return Hardware_(brand: "Apple", manufacture: "Apple", name: "iPhone 13",  productName: "iPhone")
}

private func createSoftware() -> Software_ {
    return Software_(versionName: "17", versionCode: "17", name: "iOS 17.1")
}
