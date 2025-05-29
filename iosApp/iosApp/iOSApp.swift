import SwiftUI
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate
    
    @Environment(\.scenePhase)
    var scenePhase: ScenePhase
    
    var rootHolder: RootHolder { appDelegate.rootHolder }
    
    init() {
        SharedSDK.shared.initializeSdk(
            backendUrl: "https://api-stage.vpn.run/",
            environment: "development",
            localeCode: "ru",
            deviceInfo: createDeviceInfo(),
            appVersion: createAppVersion(),
            isDebug: true,
            appInternalDirectory: "somedirectory",
            networkStatisticsManager: DefaultNetworkStatisticsManager(),
            analyticsProviders: Array(),
            reportProviders: Array(),
            vpnConnectionFactory: DefaultVpnConnectionFactory(),
            additionalModules: nil
        )
    }
    
	var body: some Scene {
		WindowGroup {
            ContentView(component: rootHolder.root)
                .onChange(of: scenePhase) { newPhase in
                    switch newPhase {
                    case .background: LifecycleRegistryExtKt.stop(rootHolder.lifecycle)
                    case .inactive: LifecycleRegistryExtKt.pause(rootHolder.lifecycle)
                    case .active: LifecycleRegistryExtKt.resume(rootHolder.lifecycle)
                    @unknown default: break
                    }
                }
		}
	}
    
    private func createAppVersion() -> AppVersion {
        return AppVersion(versionCode: 1, versionName: "1.0.0", platform: "iOS")
    }
}
