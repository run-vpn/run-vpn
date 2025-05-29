@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
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
            baseName = "serverlist"
        }
    }

    @Suppress("UNUSED_VARIABLE") sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.tea)
                implementation(projects.data.common)
                implementation(projects.data.servers)
                implementation(projects.data.device)
                implementation(projects.data.settings)
                implementation(projects.data.connection)
                implementation(projects.core.common)
                implementation(projects.feature.common)

                implementation(libs.ktor.core)
                implementation(libs.decompose.core)
                implementation(libs.koin.core)
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
    namespace = "com.runvpn.app.feature.serverlist"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
