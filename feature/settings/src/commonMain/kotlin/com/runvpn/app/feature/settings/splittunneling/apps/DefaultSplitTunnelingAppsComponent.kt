package com.runvpn.app.feature.settings.splittunneling.apps

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.doOnResume
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.SplitTunnelingMode
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.domain.usecases.GetSortedApplicationsListUseCase
import com.runvpn.app.tea.createCommonDialog
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import com.runvpn.app.tea.dialog.DialogMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

internal class DefaultSplitTunnelingAppsComponent(
    componentContext: ComponentContext,
    private val componentFactory: DecomposeComponentFactory,
    private val appSettingsRepository: AppSettingsRepository,
    private val getApplicationsListUseCase: GetSortedApplicationsListUseCase,
    private val vpnConnectionManager: VpnConnectionManager,
    private val output: (SplitTunnelingAppsComponent.Output) -> Unit,
) : SplitTunnelingAppsComponent, ComponentContext by componentContext {

    private var allInstalledApps = ArrayList<SplitTunnelingApplication>()
    private var excludedApps = ArrayList<SplitTunnelingApplication>()

    private val coroutineScope = createCoroutineScope()

    private val dialogNavigation = SlotNavigation<DialogConfig>()

    override val dialog: Value<ChildSlot<*, SimpleDialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = DialogConfig.serializer(),
        // persistent = false, // Disable navigation state saving, if needed
        handleBackButton = true, // Close the dialog on back button press
        childFactory = ::createDialogChild
    )


    private val _state = MutableValue(
        SplitTunnelingAppsComponent.State(
            loading = true,
            splitMode = SplitTunnelingMode.entries[appSettingsRepository.splitMode],
            excludedApps = listOf(),
            allApps = listOf()
        )
    )

    override val state: Value<SplitTunnelingAppsComponent.State>
        get() = _state


    private val currentSplitMode = SplitTunnelingMode.entries[appSettingsRepository.splitMode]
    private var currentExcludedApps: Set<SplitTunnelingApplication>? = null


    private val backCallback = BackCallback() {
        onBackClick()
    }

    init {
        backHandler.register(backCallback)
        lifecycle.doOnResume {
            Logger.i("SplitTunneling, onResume")
            coroutineScope.launch(Dispatchers.Default) {
                val allApps = getApplicationsListUseCase()
                onLoadApps(allApps)
            }
        }

    }

    private fun createDialogChild(
        childConfig: DialogConfig,
        childComponentContext: ComponentContext
    ): SimpleDialogComponent {
        return when (childConfig) {
            is DialogConfig.AlertDialog -> componentFactory.createCommonDialog(
                childComponentContext,
                dialogMessage = childConfig.dialogMessage,
                onConfirm = childConfig.onConfirm,
                onDismiss = childConfig.onDismiss
            )
        }
    }

    private fun onLoadApps(installedApps: List<SplitTunnelingApplication>) {
        Logger.i("SplitTunneling, onLoadApps $installedApps")
        allInstalledApps.clear()
        allInstalledApps.addAll(installedApps)

        excludedApps.clear()
        excludedApps.addAll(
            appSettingsRepository.excludedPackageIds.map { excludedPackageName ->
                allInstalledApps.find { it == excludedPackageName }
            }.filterNotNull()
        )
        currentExcludedApps = excludedApps.toSet()
        updateLists()
    }

    override fun onAddApp(app: SplitTunnelingApplication) {
        appSettingsRepository.addExcludedApplication(app)
        excludedApps.add(app)
        updateLists()
    }

    override fun onRemoveApp(app: SplitTunnelingApplication) {
        appSettingsRepository.removeExcludedPackageId(app)
        excludedApps.remove(app)
        updateLists()
    }

    override fun onBackClick() {
        checkNotNull(currentExcludedApps)
        if (vpnConnectionManager.connectionStatus.value.isDisconnected().not() &&
            (currentSplitMode != _state.value.splitMode || currentExcludedApps != excludedApps.toSet())
        ) {
            showReconnectDialog()
        } else {
            output(SplitTunnelingAppsComponent.Output.OnBack)
        }
    }

    override fun onChangeSplitMode(mode: SplitTunnelingMode) {
        appSettingsRepository.splitMode = mode.ordinal
        _state.value = state.value.copy(splitMode = mode)
    }

    private fun showReconnectDialog() {
        dialogNavigation.activate(
            DialogConfig.AlertDialog(
                DialogMessage.ReconnectVPN,
                onConfirm = {
                    vpnConnectionManager.reconnectToLatestServer()
                    dialogNavigation.dismiss()
                    output(SplitTunnelingAppsComponent.Output.OnBack)
                },
                onDismiss = {
                    dialogNavigation.dismiss()
                    output(SplitTunnelingAppsComponent.Output.OnBack)
                })
        )
    }

    private fun updateLists() {
        Logger.i("SplitTunneling, updateLists")
        val allApps =
            allInstalledApps.filter { !excludedApps.any { excluded -> excluded.packageName == it.packageName } }
        _state.value = state.value.copy(
            loading = false,
            allApps = allApps,
            excludedApps = excludedApps.sortedWith(compareBy({ it.isSystem }, { it.name }))
        )
    }

    @Serializable
    sealed interface DialogConfig {

        @Serializable
        data class AlertDialog(
            val dialogMessage: DialogMessage,
            val onConfirm: () -> Unit,
            val onDismiss: () -> Unit
        ) : DialogConfig

    }

}
