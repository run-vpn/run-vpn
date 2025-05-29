//
//  ServersListScreen.swift
//  iosApp
//
//  Created by LocalAdmin on 28.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ServersListScreen: View {
    let component: ServerListComponent
    
    @StateValue
    var state: ServerListFeature.State
    
    init(component: ServerListComponent) {
        self.component = component
        self._state = StateValue(component.state)
    }
    
    var body: some View {
        VStack(alignment: .leading) {
            //            Text("Servers")
            //                .font(.title)
            //                .fontWeight(.medium)
            //                .padding(.leading)
            ScrollView {
                LazyVStack(alignment: .leading) {
                    if (state.customServers.count > 0) {
                        Text("Custom servers")
                            .font(.headline)
                            .padding(.leading)
                            .padding(.bottom)
                        
                        ForEach(state.customServers, id: \.id) {server in
                            ServerCellView(
                                server: server,
                                onFavoriteClick: {
                                    component.onSetServerFavouriteClicked(
                                        server: server,
                                        isFavourite: true
                                    )
                                }
                            )
                            .padding(.horizontal)
                        }
                        
                        Spacer(minLength: 20)
                    }
                    
                    Text("Remote servers")
                        .font(.headline)
                        .padding(.leading)
                        .padding(.bottom)
                    ForEach(state.allServers, id: \.id) { server in
                        ServerCellView(
                            server: server,
                            onFavoriteClick: {
                                component.onSetServerFavouriteClicked(
                                    server: server,
                                    isFavourite: server.favourite == nil
                                )
                            }
                        )
                        .padding(.horizontal)
                        .onTapGesture {
                            self.component.onServerClicked(server: server)
                        }
                    }
                }
            }
        }
    }
}


#Preview {
    ServersListScreen(component: FakeServerListComponent())
}
