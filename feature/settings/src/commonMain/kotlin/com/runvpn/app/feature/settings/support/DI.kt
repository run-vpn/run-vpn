package com.runvpn.app.feature.settings.support

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.feature.settings.support.faq.DefaultFaqComponent
import com.runvpn.app.feature.settings.support.faq.FaqComponent
import com.runvpn.app.feature.settings.support.feedback.DefaultFeedbackComponent
import com.runvpn.app.feature.settings.support.feedback.FeedbackComponent
import com.runvpn.app.feature.settings.support.main.DefaultSupportComponent
import com.runvpn.app.feature.settings.support.main.SupportComponent
import com.runvpn.app.feature.settings.support.reportproblem.DefaultReportProblemComponent
import com.runvpn.app.feature.settings.support.reportproblem.ReportProblemComponent
import com.runvpn.app.feature.settings.update.DefaultUpdateComponent
import com.runvpn.app.feature.settings.update.UpdateComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get

fun DecomposeComponentFactory.createSupportComponent(
    componentContext: ComponentContext
): SupportComponent {
    return DefaultSupportComponent(componentContext, get(), get(), get())
}

fun DecomposeComponentFactory.createFaqComponent(
    componentContext: ComponentContext
): FaqComponent {
    return DefaultFaqComponent(componentContext, get())
}

fun DecomposeComponentFactory.createReportProblemComponent(
    componentContext: ComponentContext,
): ReportProblemComponent {
    return DefaultReportProblemComponent(componentContext, get(), get())
}

fun DecomposeComponentFactory.createFeedbackComponent(
    componentContext: ComponentContext,
): FeedbackComponent {
    return DefaultFeedbackComponent(componentContext, get())
}

fun DecomposeComponentFactory.createUpdateComponent(
    componentContext: ComponentContext,
    message: String,
    onDismiss: () -> Unit
): UpdateComponent {
    return DefaultUpdateComponent(
        componentContext,
        get(),
        get(),
        get(),
        message = message,
        onDismiss = onDismiss
    )
}
