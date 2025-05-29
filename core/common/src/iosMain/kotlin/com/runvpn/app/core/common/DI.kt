package com.runvpn.app.core.common

import org.koin.dsl.module

actual val coreCommonModule = module {
    includes(sharedCommonModule)

    single<ClipboardManager> { IosClipboardManager() }
    single<VibratorManager> { IosVibratorManager() }
    single<UriManager> { IosUriManager() }
}
