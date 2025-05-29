package com.runvpn.app.core.di

import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named


val authorizedKtorfitQualifier: Qualifier = named("AuthorizedKtorfit")

val appInternalDirectoryQualifier: Qualifier = named("fileDir")
val appPackageNameQualifier: Qualifier = named("appPackage")
