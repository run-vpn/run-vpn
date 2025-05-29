package com.runvpn.app.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule = module {
//    val settings = createIosSettings()
    val defaults = createNSUserDefaults()

    single<Settings> { NSUserDefaultsSettings(defaults) }
    single<ObservableSettings> { NSUserDefaultsSettings(defaults) }
}

private fun createNSUserDefaults(): NSUserDefaults {
    return NSUserDefaults("app_settings")
}
