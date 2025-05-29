package com.runvpn.app.feature.common.browser

import com.arkivanov.decompose.ComponentContext

class DefaultBrowserComponent(
    componentContext: ComponentContext,
    override val url: String
) : BrowserComponent, ComponentContext by componentContext

