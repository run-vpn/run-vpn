//
//  MainScreen.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct MainScreen: View {
    let component: MainComponent
    
    @StateValue
    private var childStack: ChildStack<AnyObject, MainComponentChild>
    
    init(component: MainComponent) {
        self.component = component
        
        self._childStack = StateValue(component.activeChild)
    }
    
    var body: some View {
        ZStack {
            Color(.blue).ignoresSafeArea()
            
            VStack(spacing: 0) {
                CommonTabBar(
                    navigationStack: _childStack,
                    tabItemsBuilder: createTabItem,
                    destinationViewBuilder: createView(by:)
                )
                .ignoresSafeArea()
            }
        }
    }
    
    /**
     * Creates a tab item for component.
     */
    private func createTabItem(for child: MainComponentChild) -> TabViewItem {
        switch (child) {
        case _ as MainComponentChildHome:
            return TabViewItem(
                title: "Home",
                image: UIImage(systemName: "house")!,
                action: { component.onHomeClicked() }
            )
        case _ as MainComponentChildServers:
            return TabViewItem(
                title: "Servers",
                image: UIImage(systemName: "xserve")!,
                action: { component.onServersClicked() }
            )
        case _ as MainComponentChildProfile:
            return TabViewItem(
                title: "Profile",
                image: UIImage(systemName: "person")!,
                action: { component.onProfileClicked() }
            )
        case _ as MainComponentChildSettings:
            return TabViewItem(
                title: "Settings",
                image: UIImage(systemName: "gearshape")!,
                action: { component.onSettingsClicked() }
            )
        default:
            return TabViewItem(title: "Pokemons", image: UIImage(systemName: "house")!, action: {})
        }
    }
    
    /**
     * Creates a view for component.
     */
    @ViewBuilder private func createView(by child: MainComponentChild) -> some View {
        switch (child) {
        case let child as MainComponentChildHome:
            HomeScreen(component: child.component)
        case let child as MainComponentChildServers:
            ServersTabScreen(component: child.component)
        case let child as MainComponentChildProfile:
            ProfileScreen()
        case let child as MainComponentChildSettings:
            SettingsScreen()
        default:
            Text("default")
        }
    }
}

#Preview {
    MainScreen(component: FakeMainComponent())
}
