package com.runvpn.app.feature.settings.support.main

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.tea.dialog.DialogManager
import com.runvpn.app.tea.dialog.RootDialogConfig
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.navigation.RootChildConfig
import com.runvpn.app.tea.navigation.RootRouter

internal class DefaultSupportComponent(
    componentContext: ComponentContext,
    private val rootRouter: RootRouter,
    private val dialogManager: DialogManager,
    private val messageService: MessageService
) : SupportComponent, ComponentContext by componentContext {

    override fun onBack() {
        rootRouter.pop()
    }

    override fun onFaqClick() {
        messageService.showMessage(AppMessage.NotImplemented())
//        rootRouter.open(RootChildConfig.Faq)
    }

    override fun onSupportChatClick() {
        dialogManager.showDialog(RootDialogConfig.Support)
    }

    override fun onReportProblemClick() {
        rootRouter.open(RootChildConfig.ReportProblem)
    }

    override fun onFeedbackClick() {
        rootRouter.open(RootChildConfig.Feedback)
    }
}

