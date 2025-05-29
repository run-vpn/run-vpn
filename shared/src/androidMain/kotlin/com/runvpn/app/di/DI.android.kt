package com.runvpn.app.di

import android.content.Context
import com.runvpn.app.Constants.SHARED_PREFS_NAME
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module

actual val platformModule = module {
    single<Settings> {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences(
                SHARED_PREFS_NAME,
                Context.MODE_PRIVATE
            )
        )
    }
    single<ObservableSettings> {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences(
                SHARED_PREFS_NAME,
                Context.MODE_PRIVATE
            )
        )
    }
}
