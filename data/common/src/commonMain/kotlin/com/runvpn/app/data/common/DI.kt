package com.runvpn.app.data.common

import com.runvpn.app.core.di.authorizedKtorfitQualifier
import com.runvpn.app.data.common.data.DefaultCommonPrefsRepository
import com.runvpn.app.data.common.data.DefaultDataCommonRepository
import com.runvpn.app.data.common.data.api.ReviewApi
import com.runvpn.app.data.common.domain.CommonPrefsRepository
import com.runvpn.app.data.common.domain.DataCommonRepository
import com.runvpn.app.data.common.domain.usecases.CheckReviewRequiredUseCase
import com.runvpn.app.data.common.domain.usecases.SendReviewUseCase
import com.runvpn.app.data.common.domain.usecases.UpdateReviewShownDateUseCase
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.dsl.module


val dataCommonModule = module {
    single {
        get<Ktorfit>(authorizedKtorfitQualifier)
            .create<ReviewApi>()
    }

    single<DataCommonRepository> {
        DefaultDataCommonRepository(get())
    }

    single<CommonPrefsRepository> { DefaultCommonPrefsRepository(get()) }

    factory { CheckReviewRequiredUseCase(get(), get()) }
    factory { SendReviewUseCase(get()) }
    factory { UpdateReviewShownDateUseCase(get()) }


}
