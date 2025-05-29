package com.runvpn.app.data.connection

import com.runvpn.app.core.di.authorizedKtorfitQualifier
import com.runvpn.app.data.connection.data.DefaultDnsServersRepository
import com.runvpn.app.data.connection.data.DnsServersApi
import com.runvpn.app.data.connection.domain.AddDnsServerUseCase
import com.runvpn.app.data.connection.domain.ConnectToNextServerUseCase
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.connection.domain.DeleteDnsServerUseCase
import com.runvpn.app.data.connection.domain.DnsServersRepository
import com.runvpn.app.data.connection.domain.SetDnsServerUseCase
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.module.Module
import org.koin.dsl.module

internal val dataConnectionSharedModule = module {
    //apis
    single {
        get<Ktorfit>(authorizedKtorfitQualifier)
            .create<DnsServersApi>()
    }

    single { VpnConnectionManager(get(), get(), get()) }

    // UseCase's
    factory { ConnectToVpnUseCase(get(), get()) }
    factory { SetDnsServerUseCase(get()) }
    factory { AddDnsServerUseCase(get(), get(), get()) }
    factory { DeleteDnsServerUseCase(get()) }
    single { ConnectToNextServerUseCase(get(), get(), get(), get()) }

    // Repositories
    single<DnsServersRepository> { DefaultDnsServersRepository(get(), get()) }
}

expect val dataConnectionModule: Module
