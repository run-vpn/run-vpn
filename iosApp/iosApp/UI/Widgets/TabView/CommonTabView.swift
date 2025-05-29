//
//  CommonTabView.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import UIKit
import shared

class HashTableContainer<Key: Hashable, Value: Any> {
    var hashTable: Dictionary<Key, Value> = Dictionary()
}

class NavigationMap {
    var tabTypeToTabIndex: [String:Int] = [:]
}

let tabTypeToIndexMap = NavigationMap()
let tabItemsMap = HashTableContainer<Int, TabViewItem>()

/**
 * Specialized TabBar for Decompose navigation.
 * See documentation in the readme.
 */
struct CommonTabBar<T: AnyObject, Content: View>: UIViewControllerRepresentable {

    typealias UIViewControllerType = UITabBarController
    
    var navigationStack: StateValue<ChildStack<AnyObject, T>>
    var tabItemsBuilder: (T) -> TabViewItem
    var destinationViewBuilder: (T) -> Content
    
    private var stack: [Child<AnyObject, T>] {
        navigationStack.wrappedValue.items
    }
    
    func makeCoordinator() -> Coordinator {
        return Coordinator()
    }
    
    func makeUIViewController(context: Context) -> UITabBarController {
        let tabBarController = UITabBarController()
        
        tabBarController.viewControllers = createTabViewControllers()
        tabBarController.delegate = context.coordinator
        
        return tabBarController
    }
    
    func updateUIViewController(_ navigationController: UITabBarController, context: Context) {
        let stringType = "\(type(of: self.navigationStack.wrappedValue.active.instance))"
        let index = tabTypeToIndexMap.tabTypeToTabIndex[stringType] ?? 0

        navigationController.selectedIndex = index
    }
    
    private func createTabViewControllers() -> [UIHostingController<Content>] {
        var viewControllers = [UIHostingController<Content>]()
        
        for (index, child) in stack.reversed().enumerated() {
            if let instance = child.instance {
                let stringType = "\(type(of: instance))"
                
                let tab = UIHostingController(rootView: destinationViewBuilder(instance))
                let tabItem = tabItemsBuilder(instance)
                
                tabTypeToIndexMap.tabTypeToTabIndex[stringType] = index
                tabItemsMap.hashTable[tab.hash] = tabItem
                
                tab.tabBarItem = UITabBarItem(title: tabItem.title, image: tabItem.image, tag: index)
                viewControllers.append(tab)
            }
        }
        
        return viewControllers
    }
    
    class Coordinator: NSObject, UITabBarControllerDelegate {
        
        func tabBarController(_ tabBarController: UITabBarController, didSelect viewController: UIViewController) {}
        
        func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
            tabItemsMap.hashTable[viewController.hash]?.action()
            
            // prevent default event handler
            return false
        }
    }
}
