package com.runvpn.app.tea.navigation

interface RootRouter {

    fun open(config: RootChildConfig)

    fun replace(config:RootChildConfig)

    fun pop()
}
