package com.runvpn.app.data.settings

import com.runvpn.app.core.di.appPackageNameQualifier
import com.runvpn.app.data.settings.data.DefaultAppSettingsRepository
import com.runvpn.app.data.settings.data.DefaultUserSettingsRepository
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.data.settings.domain.usecases.GetSortedApplicationsListUseCase
import org.koin.core.module.Module
import org.koin.dsl.module


val dataSettingsModule = module {
    includes(settingsPlatformModule)
    single<AppSettingsRepository> { DefaultAppSettingsRepository(get(), get()) }
    single<UserSettingsRepository> { DefaultUserSettingsRepository(get(), get()) }
    single<GetSortedApplicationsListUseCase> {
        GetSortedApplicationsListUseCase(
            get(),
            get(appPackageNameQualifier)
        )
    }
}

expect val settingsPlatformModule: Module
