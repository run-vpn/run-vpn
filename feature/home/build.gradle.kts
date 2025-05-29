@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktorfit)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate()

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
            baseName = "home"
            export(libs.essenty)
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
                //put your multiplatform dependencies here
                implementation(projects.core.tea)
                implementation(projects.core.common)
                implementation(projects.feature.common)
                implementation(projects.core.analytics)
                implementation(projects.data.common)
                implementation(projects.data.servers)
                implementation(projects.data.settings)
                implementation(projects.data.device)
                implementation(projects.data.connection)

                implementation(libs.koin.core)
                implementation(libs.decompose.core)
                implementation(libs.logs.kermit)
                implementation(libs.coroutines.core)
                implementation(libs.kotlin.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "com.runvpn.app.feature.home"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
