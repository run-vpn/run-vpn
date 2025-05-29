package com.runvpn.app.data.settings

import com.runvpn.app.data.settings.domain.repositories.DeviceApplicationsRepository
import org.koin.dsl.module

actual val settingsPlatformModule = module {
    single<DeviceApplicationsRepository> { AndroidApplicationRepository(get()) }
}
