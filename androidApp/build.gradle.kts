import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.util.regex.Pattern

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    alias(libs.plugins.sentry)
}

// return in lowercase
val currentFlavor: String
    get() {
        print("start:" + gradle.startParameter)
        val taskRequestsStr = gradle.startParameter.taskRequests.toString()
        val nameGroup = 1
        val pattern: Pattern = if (taskRequestsStr.contains("assemble")) {
            Pattern.compile("assemble(\\w+)(Release|Debug)")
        } else if (taskRequestsStr.contains("publishMaven")) {
            Pattern.compile("publishMaven(\\w+)(Release|Debug)PublicationTo")
        } else {
            Pattern.compile("bundle(\\w+)(Release|Debug)")
        }

        val matcher = pattern.matcher(taskRequestsStr)
        val flavor = if (matcher.find()) {
            matcher.group(nameGroup).lowercase()
        } else {
            println("NO FLAVOR FOUND: $taskRequestsStr")
            ""
        }
        println("flavor: $flavor")
        return flavor
    }

val isUseProxyLib: Boolean = !currentFlavor.contains("noproxy")

android {
    signingConfigs {
        val keystoreFilePath = if (extra.has("rls_keystore_file")) {
            extra.get("rls_keystore_file") as? String
        } else null

        if (!keystoreFilePath.isNullOrEmpty()) {
            create("release") {
                keyAlias = (extra["rls_keystore_alias"] as String)
                keyPassword = (extra["rls_key_password"] as String)
                storeFile = file(extra["rls_keystore_file"] as String)
                storePassword = extra["rls_keystore_password"] as String
            }
        }
    }

    namespace = "com.runvpn.app.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "run.vpn.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 31
        versionName = "1.2.3"
        archivesName = "RunVPN-$versionName"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    sourceSets {
        named("main") {
            jniLibs.srcDirs("libs")
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false

            buildConfigField("long", "VERSION_CODE", "${defaultConfig.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
            buildConfigField("String", "BACKEND_URL", "\"https://api.secureconnector.org/\"")
//            buildConfigField("String", "BACKEND_URL", "\"https://api-stage.vpn.run/\"")
//            buildConfigField("String", "BACKEND_URL", "\"https://api-dev.vpn.run/\"")
            buildConfigField("String", "ENVIRONMENT", "\"production\"")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            if (signingConfigs.names.contains("release")) {
                signingConfig = signingConfigs.getByName("release")
            }
            applicationIdSuffix = ".rls"
        }
        debug {
            isMinifyEnabled = false

            buildConfigField("long", "VERSION_CODE", "${defaultConfig.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
            buildConfigField("String", "BACKEND_URL", "\"https://api-stage.vpn.run/\"")
//            buildConfigField("String", "BACKEND_URL", "\"https://api.secureconnector.org/\"")
            buildConfigField("String", "ENVIRONMENT", "\"development\"")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    flavorDimensions += "proxy"
    flavorDimensions += "source"

    productFlavors {
        create("noproxy") {
            dimension = "proxy"
        }
        create("proxy") {
            dimension = "proxy"
            versionNameSuffix = ""
            applicationIdSuffix = ".ps"
        }
        create("gp") {
            dimension = "source"

            buildConfigField("String", "APP_SOURCE", "\"Google Play\"")
        }
        create("huawei") {
            dimension = "source"
            applicationId = "com.runvpn.app.android"

            buildConfigField("String", "APP_SOURCE", "\"Huawei\"")
        }
        create("apk") {
            dimension = "source"
            applicationId = "com.runvpn.app.android"
            applicationIdSuffix = ".apk"

            buildConfigField("String", "APP_SOURCE", "\"apk\"")
        }
    }

    afterEvaluate {
        gradle.extra["use_proxy"] = isUseProxyLib
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to "*.aar")))

    implementation(projects.shared)
    implementation(projects.core.common)
    implementation(projects.core.analytics)
    implementation(projects.core.tea)
    implementation(projects.core.network)
    implementation(projects.droid.ui)
    implementation(projects.data.servers)
    implementation(projects.feature.home)
    implementation(projects.feature.serverlist)
    implementation(projects.feature.profile)
    implementation(projects.feature.settings)
    implementation(projects.feature.authorization)
    implementation(projects.feature.subscription)
    implementation(projects.feature.common)
    implementation(projects.feature.trafficmodule)
    implementation(projects.data.common)
    implementation(projects.data.db)
    implementation(projects.data.settings)
    implementation(projects.data.device)
    implementation(projects.data.connection)
    implementation(projects.data.subscription)

//    implementation(libs.proxy.sdk.wrapper.api)
//    if (isUseProxyLib) {
//        implementation(libs.proxy.sdk.wrapper.db)
//        implementation(libs.proxy.sdk.wrapper)
//    } else {
//        implementation(libs.proxy.sdk.wrapper.stub)
//    }

    implementation(projects.openvpnlib)

    implementation(libs.kotlin.datetime)

    implementation(libs.koin.core)

    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)

    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.glance.appwidgets)
    implementation(libs.glance.material3)
    implementation(libs.glance.hostpreview)

    implementation(libs.androidx.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil)
    implementation(libs.flagkit)
    implementation(libs.logs.kermit)
    implementation(libs.zxing)

    implementation(libs.quickie.bundled)
    implementation(libs.flagkit.android)

    implementation(libs.decompose.core)
    implementation(libs.decompose.ext.android)
    implementation(libs.decompose.ext.compose)
    implementation(libs.tunnel)
    implementation(libs.relinker)
    implementation(libs.cryptacular)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

//    debugImplementation(libs.tools.leakcanary)

//    debugImplementation(libs.compose.ui.tooling)
}

sentry {
    debug.set(true)

    projectName.set("vpn-run-android")
    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))

    ignoredBuildTypes.set(setOf("debug", "release"))

    autoInstallation {
        enabled.set(false)
    }
}
