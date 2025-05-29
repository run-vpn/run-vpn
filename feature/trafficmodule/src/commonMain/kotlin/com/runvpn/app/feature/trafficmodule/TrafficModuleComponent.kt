package com.runvpn.app.feature.trafficmodule

import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

interface TrafficModuleComponent {
    val state: Value<TrafficModuleFeature.State>

    fun onBack()
    fun onLookLogs()

    fun logsReceiver(scope: CoroutineScope): SharedFlow<Any>
    fun getDbLogs(): List<Any>

    fun getStatistics(): Any

    fun getStatus(): Any

    sealed interface Output {
        data object LookLogsRequested : Output
        data object OnBack : Output
    }
}
