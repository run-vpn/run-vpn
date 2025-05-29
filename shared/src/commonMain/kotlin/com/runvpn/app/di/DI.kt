package com.runvpn.app.di

import com.runvpn.app.data.common.dataCommonModule
import com.runvpn.app.data.connection.dataConnectionModule
import com.runvpn.app.data.device.dataAuthorizationModule
import com.runvpn.app.data.device.dataDeviceModule
import com.runvpn.app.data.device.dataTrafficModuleModule
import com.runvpn.app.data.device.dataUpdateModule
import com.runvpn.app.data.device.dataUserModule
import com.runvpn.app.data.servers.dataServersModule
import com.runvpn.app.data.settings.dataSettingsModule
import com.runvpn.app.data.subscription.dataSubscriptionsModule
import com.runvpn.app.db.databaseModule
import com.runvpn.app.presentation.root.rootModule
import com.runvpn.app.tea.coreTeaModule
import org.koin.core.module.Module


val allModules = listOf(
    coreTeaModule,
    databaseModule,
    dataSettingsModule,
    dataSubscriptionsModule,
    dataCommonModule,
    dataServersModule,
    dataAuthorizationModule,
    dataUserModule,
    dataUpdateModule,
    dataTrafficModuleModule,
    dataDeviceModule,
    dataConnectionModule,
    rootModule
)

expect val platformModule: Module
