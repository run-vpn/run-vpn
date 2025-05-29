package com.runvpn.app.tea.decompose

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

class DecomposeComponentFactory(private val koin: Koin) : KoinComponent {

    override fun getKoin() = koin

}

