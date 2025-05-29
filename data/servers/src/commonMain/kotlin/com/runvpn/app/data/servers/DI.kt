package com.runvpn.app.data.servers

import com.runvpn.app.core.di.authorizedKtorfitQualifier
import com.runvpn.app.data.servers.data.DefaultServersRepository
import com.runvpn.app.data.servers.data.ServersApi
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.servers.domain.usecases.CreateCustomServerUseCase
import com.runvpn.app.data.servers.domain.usecases.ExtractSocks5FromUrlUseCase
import com.runvpn.app.data.servers.domain.usecases.ExtractVlessConfigFromUrlUseCase
import com.runvpn.app.data.servers.domain.usecases.GetSuggestedServersUseCase
import com.runvpn.app.data.servers.domain.usecases.PrepareOpenVpnConfigUseCase
import com.runvpn.app.data.servers.domain.usecases.SetCurrentServerUseCase
import com.runvpn.app.data.servers.domain.usecases.SetServerFavoriteUseCase
import com.runvpn.app.data.servers.domain.usecases.ValidateOpenVpnConfigUseCase
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.dsl.module

val dataServersModule = module {
    single<ServersApi> { get<Ktorfit>(authorizedKtorfitQualifier).create() }
    single<ServersRepository> { DefaultServersRepository(get(), get(), get(), get(), get()) }

    // UseCase's
    factory { PrepareOpenVpnConfigUseCase(get()) }
    factory { ValidateOpenVpnConfigUseCase() }
    factory { SetServerFavoriteUseCase(get()) }
    factory { SetCurrentServerUseCase(get()) }
    factory { GetSuggestedServersUseCase(get(), get()) }
    factory { ExtractVlessConfigFromUrlUseCase() }
    factory { CreateCustomServerUseCase() }
    factory { ExtractSocks5FromUrlUseCase() }
}


