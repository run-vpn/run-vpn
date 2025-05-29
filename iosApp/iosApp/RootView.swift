import SwiftUI
import shared

struct ContentView: View {
    let component: RootComponent
    
    @StateValue
    private var childStack: ChildStack<AnyObject, RootComponentChild>
    
    init(component: RootComponent) {
        self.component = component
        self._childStack = StateValue(component.childStack)
    }
    
    var body: some View {
        StackView(
            stackValue: _childStack,
            getTitle: { _ in "" },
            onBack: { _ in },
            childContent: { c in
                if let component = (c as? RootComponentChildPrivacyPolicy)?.component {
                    PrivacyPolicyScreen(component: component)
                } else if let component = (c as? RootComponentChildMain)?.component {
                    MainScreen(component: component)
                }
            }
        )
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
//        ContentView(component: FakeRootComponent())
        Text("Some text")
	}
}
