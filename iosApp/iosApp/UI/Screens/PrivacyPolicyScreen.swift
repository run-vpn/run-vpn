//
//  PrivacyPolicyScreen.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct PrivacyPolicyScreen: View {
    let component: WelcomeComponent
    
    var body: some View {
        VStack {
            Text("WelcomeScreen (privacy policy)")
            Button(action: { component.onConfirmClick() }, label: {
                Text("Agree")
            })
            .padding(.top)
        }
    }
}

#Preview {
    PrivacyPolicyScreen(component: FakeWelcomeComponent())
}
