package com.runvpn.app.feature.subscription

import com.arkivanov.decompose.ComponentContext
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.subscription.domain.entities.ChosenSubscription
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost
import com.runvpn.app.feature.subscription.aboutdevice.AboutDeviceComponent
import com.runvpn.app.feature.subscription.aboutdevice.DefaultAboutDeviceComponent
import com.runvpn.app.feature.subscription.activate.DefaultSubscriptionActivateComponent
import com.runvpn.app.feature.subscription.activate.SubscriptionActivateComponent
import com.runvpn.app.feature.subscription.activate.shareapkdialog.DefaultShareApkFileComponent
import com.runvpn.app.feature.subscription.activate.shareapkdialog.ShareApkFileComponent
import com.runvpn.app.feature.subscription.activate.sharecodedialog.DefaultShareActivationCodeComponent
import com.runvpn.app.feature.subscription.activate.sharecodedialog.ShareActivationCodeComponent
import com.runvpn.app.feature.subscription.balancerefill.BalanceRefillComponent
import com.runvpn.app.feature.subscription.balancerefill.DefaultBalanceRefillComponent
import com.runvpn.app.feature.subscription.buy.BuySubscriptionComponent
import com.runvpn.app.feature.subscription.buy.DefaultBuySubscriptionComponent
import com.runvpn.app.feature.subscription.confirm.DefaultSubscriptionConfirmComponent
import com.runvpn.app.feature.subscription.confirm.SubscriptionConfirmComponent
import com.runvpn.app.feature.subscription.cryptowallets.BuyWithCryptoComponent
import com.runvpn.app.feature.subscription.cryptowallets.DefaultBuyWithCryptoComponent
import com.runvpn.app.feature.subscription.info.DefaultSubscriptionInfoComponent
import com.runvpn.app.feature.subscription.info.SubscriptionInfoComponent
import com.runvpn.app.feature.subscription.promo.ActivatePromoComponent
import com.runvpn.app.feature.subscription.promo.DefaultActivatePromoComponent
import com.runvpn.app.feature.subscription.tariff.ChooseRateComponent
import com.runvpn.app.feature.subscription.tariff.DefaultChooseRateComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.component.get


fun DecomposeComponentFactory.createSubscriptionRootComponent(
    componentContext: ComponentContext
): SubscriptionRootComponent {
    return DefaultSubscriptionRootComponent(
        componentContext,
        get(),
        get(),
        get()
    )
}

fun DecomposeComponentFactory.createSubscriptionInfoComponent(
    componentContext: ComponentContext,
    onOutput: (SubscriptionInfoComponent.Output) -> Unit
): SubscriptionInfoComponent {
    return DefaultSubscriptionInfoComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        onOutput
    )
}

fun DecomposeComponentFactory.createChooseSubscriptionTariffComponent(
    componentContext: ComponentContext,
    initialChosenSubscription: ChosenSubscription,
    onOutput: (ChooseRateComponent.Output) -> Unit
): ChooseRateComponent {
    return DefaultChooseRateComponent(
        componentContext,
        initialChosenSubscription,
        get(),
        get(),
        get(),
        get(),
        onOutput
    )
}

fun DecomposeComponentFactory.createBuyWithCryptoComponent(
    componentContext: ComponentContext,
    wallets: List<WalletWithRateCost>,
    onDismissed: () -> Unit
): BuyWithCryptoComponent {
    return DefaultBuyWithCryptoComponent(componentContext, wallets, get(), onDismissed)
}

fun DecomposeComponentFactory.createBuySubscriptionComponent(
    componentContext: ComponentContext,
    output: (BuySubscriptionComponent.Output) -> Unit
): BuySubscriptionComponent {
    return DefaultBuySubscriptionComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        output
    )
}

fun DecomposeComponentFactory.createBalanceRefillComponent(
    componentContext: ComponentContext,
    cost: Double,
): BalanceRefillComponent {
    return DefaultBalanceRefillComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        cost = cost
    )
}

fun DecomposeComponentFactory.createSubscriptionActivateComponent(
    componentContext: ComponentContext,
    output: (SubscriptionActivateComponent.Output) -> Unit
): SubscriptionActivateComponent {
    return DefaultSubscriptionActivateComponent(
        componentContext,
        get(),
        get(),
        get(),
        output
    )
}

fun DecomposeComponentFactory.createSubscriptionConfirmComponent(
    componentContext: ComponentContext,
    subscriptionId: String,
    device: Device,
    output: (SubscriptionConfirmComponent.Output) -> Unit
): SubscriptionConfirmComponent {
    return DefaultSubscriptionConfirmComponent(
        componentContext,
        get(),
        get(),
        get(),
        subscriptionId,
        device,
        output
    )
}

fun DecomposeComponentFactory.createAboutDeviceComponent(
    componentContext: ComponentContext,
    device: Device,
    output: (AboutDeviceComponent.Output) -> Unit
): AboutDeviceComponent {
    return DefaultAboutDeviceComponent(
        componentContext,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        device,
        output
    )
}

fun DecomposeComponentFactory.createShareApkComponent(
    componentContext: ComponentContext,
    link: String,
    onDismissed: () -> Unit
): ShareApkFileComponent {
    return DefaultShareApkFileComponent(
        componentContext,
        get(),
        link,
        onDismissed
    )
}


fun DecomposeComponentFactory.createShareActivationCodeComponent(
    componentContext: ComponentContext,
    code: String,
    onDismissed: () -> Unit
): ShareActivationCodeComponent {
    return DefaultShareActivationCodeComponent(
        componentContext,
        get(),
        code,
        onDismissed
    )
}

fun DecomposeComponentFactory.createActivatePromoComponent(
    componentContext: ComponentContext
): ActivatePromoComponent {
    return DefaultActivatePromoComponent(
        componentContext,
        get()
    )

}
