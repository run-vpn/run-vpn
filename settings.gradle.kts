enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
    }
}

rootProject.name = "RunVPN"

/** Platform modules*/
include(":androidApp")
include(":droid:ui")

/** Shared module*/
include(":shared")

/** Core modules*/
include(":core:common")
include(":core:di")
include(":core:tea")
include(":core:network")
include(":core:analytics")

/** Data modules*/
include(":data:common")
include(":data:db")
include(":data:servers")
include(":data:settings")
include(":data:device")
include(":data:connection")
include(":data:subscription")

/** Feature modules*/
include(":feature:home")
include(":feature:serverlist")
include(":feature:settings")
include(":feature:authorization")
include(":feature:profile")
include(":feature:trafficmodule")
include(":feature:subscription")
include(":feature:common")

/** Library modules*/
include(":openvpnlib")
