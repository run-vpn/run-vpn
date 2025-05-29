package com.runvpn.app.data.settings

import com.runvpn.app.data.settings.domain.repositories.DeviceApplicationsRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual val settingsPlatformModule: Module = module {
    single<DeviceApplicationsRepository> { IosApplicationRepository() }
}
