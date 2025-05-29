package com.runvpn.app.feature.trafficmodule

//import com.android.asdk.wrapper.app.SdkManager
//import com.android.asdk.wrapper.app.logs.SdkLogsManager
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.tea.decompose.BaseComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

internal class DefaultTrafficModuleComponent(
    componentContext: ComponentContext,
    deviceRepository: DeviceRepository,
    appSettingsRepository: AppSettingsRepository,
//    private val sdkLogsManager: SdkLogsManager,
//    private val sdkManager: SdkManager,
    private val onOutput: (TrafficModuleComponent.Output) -> Unit,
) : BaseComponent<TrafficModuleFeature.State, TrafficModuleFeature.Message, TrafficModuleFeature.Dependencies>(
    initialState = TrafficModuleFeature.Logic.initialUpdate,
    restore = TrafficModuleFeature.Logic::restore,
    update = TrafficModuleFeature.Logic::update,
    dependencies = TrafficModuleFeature.Dependencies(
        deviceRepository = deviceRepository,
        settingsRepository = appSettingsRepository,
    )
), TrafficModuleComponent, ComponentContext by componentContext {

    override fun onBack() {
        onOutput(TrafficModuleComponent.Output.OnBack)
    }

    override fun onLookLogs() {
        onOutput(TrafficModuleComponent.Output.LookLogsRequested)
    }

    override fun getDbLogs() = listOf<Any>()//sdkLogsManager.getLogs()
    override fun getStatistics() =  listOf<Any>()//sdkManager.getStatistics()
    override fun getStatus() =  listOf<Any>()// sdkManager.getStatus()

    override fun logsReceiver(scope: CoroutineScope) =  MutableStateFlow(Any())//sdkLogsManager.getLogsReceiver(scope)

    init {
        deviceRepository.trafficModuleInfoData.value?.let {
            dispatch(TrafficModuleFeature.Message.TrafficResponse(it))
        }

        lifecycle.doOnResume {
            dispatch(TrafficModuleFeature.Message.Update)
        }
    }
}
