//
//  ServerCellView.swift
//  iosApp
//
//  Created by LocalAdmin on 28.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared
import FlagKit

struct ServerCellView: View {
    let server: Server
    let flag: Flag?
    let onFavoriteClick: () -> Void
    
    init(server: Server, onFavoriteClick: @escaping () -> Void) {
        self.server = server
        self.onFavoriteClick = onFavoriteClick
        if (server.iso != nil) {
            self.flag = Flag(countryCode: server.iso!)
        } else {
            self.flag = nil
        }
    }
    
    var body: some View {
        HStack(alignment: .center) {
            Image(systemName: server.favourite == false ? "star" : "star.fill")
                .resizable()
                .frame(width: 25, height: 25)
                .foregroundColor(Color("ColorPrimary"))
                .padding(.trailing)
                .onTapGesture {
                    onFavoriteClick()
                }
            if (flag != nil) {
                Image(uiImage: flag!.image(style: .roundedRect))
                    .resizable()
                    .frame(width: 28, height: 20)
            }
            if (server.country != nil && server.city != nil) {
                VStack(alignment: .leading) {
                    Text(server.country!)
                        .font(.system(size: 14))
                        .fontWeight(.bold)
                    Text(server.city!)
                        .font(.system(size: 12))
                }
            } else {
                Text(server.host)
            }
            Spacer()
        }
        .padding(.horizontal, 15)
        .padding(.vertical, 10)
        .background(.white)
        .clipShape(RoundedRectangle(cornerRadius: 15.0))
        .overlay {
            RoundedRectangle(cornerRadius: 15.0)
                .stroke(Color("DividerColor"), lineWidth: 1)
        }
    }
}

#Preview {
    ServerCellView(
        server: TestData.shared.testServer1,
        onFavoriteClick: {}
    )
}
