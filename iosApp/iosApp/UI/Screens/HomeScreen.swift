//
//  HomeScreen.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared
import FlagKit

struct HomeScreen: View {
    let component: HomeComponent
    
    @StateValue
    var state: HomeFeature.State
    
    init(component: HomeComponent) {
        self.component = component
        self._state = StateValue(component.state)
    }
    
    var body: some View {
        ZStack {
            Color("backgroundTertiary").ignoresSafeArea(edges: .top)
            Image("map")
            
            VStack(alignment: .leading) {
                trafficView()
                    .padding(20)
                
                Spacer()
                
                VStack {
                    Text(getConnectionStatusTitle(state.vpnStatus))
                        .font(.title)
                    
                    Button(
                        action: {
                            if (state.vpnStatus is ConnectionStatusDisconnected) {
                                component.onConnectClick()
                            } else {
                                component.onDisconnectClick()
                            }
                        },
                        label: {
                            Text(state.vpnStatus is ConnectionStatusDisconnected ? "Connect" : "Disconnect")
                        }
                    )
                    .buttonStyle(.bordered)
                }
                .frame(maxWidth: .infinity)
                
                Spacer()
                
                currentServerView()
                    .padding(.horizontal, 20)
                    .padding(.bottom, 20)
                    .onTapGesture {
                        component.onCurrentServerClick()
                    }
                
                suggestedServersView()
                    .padding(.bottom)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
    
    @ViewBuilder private func trafficView() -> some View {
        HStack(alignment: .center, spacing: 0) {
            VStack {
                HStack {
                    Image("ic_common_traffic")
                    VStack(alignment: .leading) {
                        Text("459 Mb")
                            .font(.system(size: 14))
                            .fontWeight(.medium)
                        Text("Traffic")
                            .font(.system(size: 10))
                    }
                }
            }
            Spacer()
            VStack {
                HStack {
                    Image("ic_common_traffic")
                    VStack(alignment: .leading) {
                        Text("4 Mb/s")
                            .font(.system(size: 14))
                            .fontWeight(.medium)
                        Text("Download")
                            .font(.system(size: 10))
                    }
                }
            }
            Spacer()
            VStack {
                HStack {
                    Image("ic_common_traffic")
                    VStack(alignment: .leading) {
                        Text("7 Mb/s")
                            .font(.system(size: 14))
                            .fontWeight(.medium)
                        Text("Upload")
                            .font(.system(size: 10))
                    }
                }
            }
        }
        .padding(15)
        .background(.white, in: RoundedRectangle(cornerRadius: 16))
    }
    
    @ViewBuilder private func currentServerView() -> some View {
        if let currentServer = state.currentServer {
            
            HStack {
                if let flag = Flag(countryCode: currentServer.iso ?? "") {
                    Image(uiImage: flag.image(style: .roundedRect))
                        .resizable()
                        .frame(width: 40, height: 28)
                } else {
                    Image(systemName: "xserve")
                }
                VStack(alignment: .leading) {
                    Text(currentServer.country ?? currentServer.host)
                        .font(.system(size: 14))
                        .fontWeight(.bold)
                    if (currentServer.city != nil) {
                        Text(currentServer.city ?? "")
                            .font(.system(size: 12))
                            .foregroundStyle(Color("textSecondary"))
                    }
                }
                Spacer()
                Image("ic_chevron_right_bg")
            }
            .padding(15)
            .background(.white, in: RoundedRectangle(cornerRadius: 16))
        }
    }
    
    @ViewBuilder private func suggestedServersView() -> some View {
        if let suggestedServers = state.suggestedServers {
            VStack(alignment: .leading) {
                Text(getSuggestedServersTitle(mode: suggestedServers.mode))
                    .font(.system(size: 14))
                    .foregroundStyle(Color("textSecondary"))
                    .padding(.horizontal, 20)
                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack(spacing: 10) {
                        ForEach(suggestedServers.servers, id: \.id) { server in
                            suggestedServerItem(server: server)
                                .onTapGesture {
                                    component.onServerClicked(server: server)
                                }
                        }
                    }
                    .padding(.horizontal, 20)
                }
                .frame(maxWidth: .infinity, maxHeight: 60)
            }
        }
    }
    
    @ViewBuilder private func suggestedServerItem(server: Server) -> some View {
        HStack {
            if let flag = Flag(countryCode: server.iso ?? "") {
                Image(uiImage: flag.image(style: .roundedRect))
                    .padding(.trailing, 10)
            } else {
                Image(systemName: "xserve")
                    .padding(.trailing, 10)
            }
            
            Text(server.country ?? server.host)
        }
        .padding()
        .background(.white, in: RoundedRectangle(cornerRadius: 12))
    }
    
    private func getSuggestedServersTitle(mode: SuggestedServersMode) -> String {
        if (mode == SuggestedServersMode.auto_) {
            return "Auto"
        } else if (mode == SuggestedServersMode.favorites) {
            return "Favorites"
        } else if (mode == SuggestedServersMode.recommended) {
            return "Recommended"
        } else if (mode == SuggestedServersMode.recent) {
            return "Recent"
        } else {
            return ""
        }
    }
    
    private func getConnectionStatusTitle(_ status: ConnectionStatus) -> String {
        if (status is ConnectionStatusConnecting) {
            return "Connecting"
        } else if (status is ConnectionStatusConnected) {
            return "Connected"
        } else if (status is ConnectionStatusError) {
            return "Connection error"
        } else if (status is ConnectionStatusDisconnected) {
            return "Disconnected"
        } else {
            return "Disconnected"
        }
    }
}

#Preview {
    HomeScreen(component: FakeHomeComponent(isConnected: false))
}
