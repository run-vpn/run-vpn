package com.runvpn.app.data.connection

import org.koin.core.module.Module
import org.koin.dsl.module

actual val dataConnectionModule: Module = module {
    includes(dataConnectionSharedModule)

    single { ExcludedAppIdsDefaults() }
}
