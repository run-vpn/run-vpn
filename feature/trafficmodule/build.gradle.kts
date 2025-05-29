@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "trafficmodule"
            isStatic = true
        }
    }

    @Suppress("UNUSED_VARIABLE") sourceSets {
        val androidMain by getting {
            dependencies {
//                implementation(libs.proxy.sdk.wrapper.api)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(projects.core.tea)
                implementation(projects.data.servers)
                implementation(projects.data.device)
                implementation(projects.data.settings)

                implementation(libs.decompose.core)
                implementation(libs.koin.core)
                implementation(libs.logs.kermit)
                implementation(libs.coroutines.core)
            }
        }
    }
}

android {
    namespace = "com.runvpn.app.feature.trafficmodule"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
