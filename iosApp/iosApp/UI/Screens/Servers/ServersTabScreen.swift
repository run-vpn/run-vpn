//
//  ServersScreen.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ServersTabScreen: View {
    let component: ServerTabComponent
    
    @StateValue
    private var childStack: ChildStack<AnyObject, ServerTabComponentChild>
    
    init(component: ServerTabComponent) {
        self.component = component
        self._childStack = StateValue(component.childStack)
    }

    var body: some View {
        StackView(
            stackValue: _childStack,
            getTitle: { _ in "" },
            onBack: { _ in },
            childContent: { c in
                if let component = (c as? ServerTabComponentChildServerList)?.serverListComponent {
                    ServersListScreen(component: component)
                }
            }
        )
    }
}

//#Preview {
//    ServersScreen(FakeServerTabComponent())
//}
