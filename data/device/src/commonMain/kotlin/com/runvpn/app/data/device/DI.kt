package com.runvpn.app.data.device

import com.runvpn.app.core.di.appInternalDirectoryQualifier
import com.runvpn.app.core.di.authorizedKtorfitQualifier
import com.runvpn.app.data.device.data.DefaultAuthorizationRepository
import com.runvpn.app.data.device.data.DefaultDeviceRepository
import com.runvpn.app.data.device.data.DefaultUpdateRepository
import com.runvpn.app.data.device.data.DefaultUserRepository
import com.runvpn.app.data.device.data.api.AuthorizationApi
import com.runvpn.app.data.device.data.api.DeviceApi
import com.runvpn.app.data.device.data.api.TrafficModuleApi
import com.runvpn.app.data.device.data.api.UpdateApi
import com.runvpn.app.data.device.data.api.UserApi
import com.runvpn.app.data.device.domain.AuthorizationRepository
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.data.device.domain.UserRepository
import com.runvpn.app.data.device.domain.usecases.auth.AuthByEmailUseCase
import com.runvpn.app.data.device.domain.usecases.auth.ConfirmEmailCodeUseCase
import com.runvpn.app.data.device.domain.usecases.auth.SendEmailConfirmationCodeUseCase
import com.runvpn.app.data.device.domain.usecases.auth.SendNewPasswordToEmailUseCase
import com.runvpn.app.data.device.domain.usecases.auth.SetUserPasswordUseCase
import com.runvpn.app.data.device.domain.usecases.auth.ValidatePasswordUseCase
import com.runvpn.app.data.device.domain.usecases.device.ChangeDeviceNameUseCase
import com.runvpn.app.data.device.domain.usecases.update.CheckAppUpdateAvailableUseCase
import com.runvpn.app.data.device.domain.usecases.device.CheckDeviceRegisterUseCase
import com.runvpn.app.data.device.domain.usecases.device.DeviceDeleteUseCase
import com.runvpn.app.data.device.domain.usecases.update.GetApplicationInfoUseCase
import com.runvpn.app.data.device.domain.usecases.user.GetUserDevicesUseCase
import com.runvpn.app.data.device.domain.usecases.device.RegisterDeviceUseCase
import com.runvpn.app.data.device.domain.usecases.user.GetUserShortDataUseCase
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.dsl.module


val dataAuthorizationModule = module {
    single {
        get<Ktorfit>(authorizedKtorfitQualifier)
            .create<AuthorizationApi>()
    }

    single<AuthorizationRepository> { DefaultAuthorizationRepository(get()) }

    factory { AuthByEmailUseCase(get(), get()) }
    factory { SendEmailConfirmationCodeUseCase(get()) }
    factory { ConfirmEmailCodeUseCase(get(), get()) }
    factory { SendNewPasswordToEmailUseCase(get()) }
    factory { ValidatePasswordUseCase() }
}


val dataUserModule = module {
    single {
        get<Ktorfit>(authorizedKtorfitQualifier)
            .create<UserApi>()
    }

    single<UserRepository> { DefaultUserRepository(get()) }

    factory { GetUserShortDataUseCase(get()) }
    factory { SetUserPasswordUseCase(get()) }
}


val dataUpdateModule = module {
    single {
        get<Ktorfit>(authorizedKtorfitQualifier)
            .create<UpdateApi>()
    }

    single<UpdateRepository> {
        DefaultUpdateRepository(
            get(appInternalDirectoryQualifier),
            get(),
            get(),
            get()
        )
    }

    factory { CheckAppUpdateAvailableUseCase(get(), get(), get()) }

    single { GetApplicationInfoUseCase(get()) }
}


val dataTrafficModuleModule = module {
    single {
        get<Ktorfit>(authorizedKtorfitQualifier)
            .create<TrafficModuleApi>()
    }
}


val dataDeviceModule = module {
    single {
        get<Ktorfit>(authorizedKtorfitQualifier)
            .create<DeviceApi>()
    }

    single<DeviceRepository> { DefaultDeviceRepository(get()) }

    factory { CheckDeviceRegisterUseCase(get()) }
    factory { RegisterDeviceUseCase(get(), get()) }

    factory { GetUserDevicesUseCase(get()) }

    factory { DeviceDeleteUseCase(get(), get()) }
    factory { ChangeDeviceNameUseCase(get()) }
}
