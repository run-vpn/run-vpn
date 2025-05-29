import io.gitlab.arturbosch.detekt.Detekt

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.detekt).apply(true)
}

allprojects {
    repositories {
        google()
        // mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
        maven { url = uri("https://jitpack.io") }

//        maven {
//            credentials {
//                username = extra["mg_art_user_r_proxy_android"] as String
//                password = extra["mg_art_pass_r_proxy_android"] as String
//            }
//            url = uri(extra["mg_art_url_proxy_android"] as String)
//            isAllowInsecureProtocol = true
//        }
    }
}

detekt {
    basePath = rootProject.path
    source.setFrom(files(rootDir))
    config.setFrom(rootProject.files("code_quality/config.yml"))
    buildUponDefaultConfig = true
    ignoreFailures = false
}

tasks.withType(Detekt::class.java).configureEach {
    reports {
        html {
            required.set(true)
            outputLocation.set(file("build/reports/detekt/report.html"))
        }
    }
    exclude {
        it.file.absolutePath.substring(project.projectDir.absolutePath.length).startsWith("/.gradle")
    }
}

tasks.withType<Detekt>().configureEach {
    exclude("**/build/**") // but exclude our legacy internal package
}
