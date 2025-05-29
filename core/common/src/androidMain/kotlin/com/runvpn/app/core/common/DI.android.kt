package com.runvpn.app.core.common

import org.koin.dsl.module

actual val coreCommonModule = module {
    includes(sharedCommonModule)

    single<ClipboardManager> { AndroidClipboardManager(get()) }
    single<VibratorManager> { AndroidVibratorManager(get()) }
    single<UriManager> { AndroidUriManager(get()) }
    single<PingHelper> { AndroidPingHelper() }
}
