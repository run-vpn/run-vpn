//
//  RootHolder.swift
//  iosApp
//
//  Created by mac-o on 24.10.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class RootHolder : ObservableObject {
    let lifecycle: LifecycleRegistry
    let root: RootComponent
    
    init() {
        lifecycle = LifecycleRegistryKt.LifecycleRegistry()
        root = SharedSDK.shared
            .getDecomposeComponents()
            .createRootComponent(componentContext: DefaultComponentContext(lifecycle: lifecycle))
        
        LifecycleRegistryExtKt.create(lifecycle)
    }
    
    deinit {
        LifecycleRegistryExtKt.destroy(lifecycle)
    }
}
