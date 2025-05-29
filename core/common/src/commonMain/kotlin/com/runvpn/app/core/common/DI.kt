package com.runvpn.app.core.common

import com.runvpn.app.core.common.domain.usecases.CheckIPv4ValidUseCase
import com.runvpn.app.core.common.domain.usecases.CheckIPv6ValidUseCase
import com.runvpn.app.core.common.domain.usecases.ValidateEmailUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

internal val sharedCommonModule: Module = module {
    factory { ValidateEmailUseCase() }
    factory { CheckIPv4ValidUseCase() }
    factory { CheckIPv6ValidUseCase() }
}

expect val coreCommonModule: Module
