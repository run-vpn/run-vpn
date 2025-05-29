@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.runvpn.app"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
        buildConfig = true
    }
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
            baseName = "shared"
            isStatic = true

            export(libs.decompose.core)
            export(libs.essenty)

            export(projects.core.analytics)
            export(projects.data.db)
            export(projects.data.connection)
            export(projects.data.device)
            export(projects.data.servers)
            export(projects.data.settings)

            export(projects.feature.home)
            export(projects.feature.serverlist)
        }
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
//                implementation(libs.proxy.sdk.wrapper.api)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(projects.core.common)
                api(projects.core.analytics)
                implementation(projects.core.di)
                implementation(projects.core.tea)
                implementation(projects.core.network)
                implementation(projects.data.common)
                api(projects.data.db)
                api(projects.data.servers)
                api(projects.data.settings)
                api(projects.data.connection)
                implementation(projects.data.subscription)
                api(projects.data.device)
                api(projects.feature.home)
                api(projects.feature.serverlist)
                implementation(projects.feature.profile)
                implementation(projects.feature.settings)
                implementation(projects.feature.authorization)
                implementation(projects.feature.subscription)
                implementation(projects.feature.trafficmodule)

                implementation(libs.koin.core)
                implementation(libs.kotlin.datetime)

                api(libs.sqldelight.runtime)

                implementation(libs.settings)
                implementation(libs.ktorfit)
                implementation(libs.ktor.core)
                implementation(libs.ktor.logging)
                implementation(libs.coroutines.core)
                implementation(libs.kotlin.serialization.json)
                implementation(libs.okio)
                implementation(libs.essenty)
                implementation(libs.decompose.core)
                implementation(libs.logs.kermit)
                implementation(libs.sentry)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val iosMain by getting {
            dependsOn(commonMain)

            dependencies {
                api(libs.decompose.core)
                api(libs.essenty)

                implementation(libs.sqldelight.native)
                implementation(libs.ktor.client.ios)
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.ktorfit.ksp)
    add("kspAndroid", libs.ktorfit.ksp)
    add("kspIosX64", libs.ktorfit.ksp)
    add("kspIosArm64", libs.ktorfit.ksp)
    add("kspIosSimulatorArm64", libs.ktorfit.ksp)
}
