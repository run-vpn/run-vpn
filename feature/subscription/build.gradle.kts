@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

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
            baseName = "subscription"
            isStatic = true
        }
    }

    @Suppress("UNUSED_VARIABLE") sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(projects.core.tea)
                implementation(projects.core.common)
                implementation(projects.feature.common)
                implementation(projects.data.device)
                implementation(projects.data.subscription)
                implementation(projects.data.settings)

                implementation(libs.kotlin.datetime)
                implementation(libs.koin.core)
                implementation(libs.decompose.core)
                implementation(libs.logs.kermit)
                implementation(libs.coroutines.core)
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
    namespace = "com.runvpn.app.feature.subscription"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
