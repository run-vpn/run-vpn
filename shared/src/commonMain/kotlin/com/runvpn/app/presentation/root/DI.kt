package com.runvpn.app.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.core.di.appInternalDirectoryQualifier
import com.runvpn.app.core.network.ktorEngine
import com.runvpn.app.domain.ClearAppCacheUseCase
import com.runvpn.app.domain.InitApplicationDataUseCase
import com.runvpn.app.domain.LogOutUserUseCase
import com.runvpn.app.domain.SwitchTokensUseCase
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import org.koin.core.component.get
import org.koin.dsl.module

val rootModule = module {
    single<HttpClient> { createHttpClient() }

    single { InitApplicationDataUseCase(get(), get(), get()) }
    single { LogOutUserUseCase(get(), get(), get(), get()) }
    single { ClearAppCacheUseCase(get(), get(), get()) }
    single { SwitchTokensUseCase(get(), get()) }
}

fun createHttpClient(): HttpClient {
    return HttpClient(ktorEngine) {


        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    co.touchlab.kermit.Logger.i(message)
                }
            }
            level = LogLevel.BODY
        }

    }
}

fun DecomposeComponentFactory.createRootComponent(
    componentContext: ComponentContext
): RootComponent {
    return DefaultRootComponent(
        componentContext,
        get(appInternalDirectoryQualifier),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
    )
}
