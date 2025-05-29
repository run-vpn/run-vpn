package com.runvpn.app.feature.trafficmodule

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.TestDataDevices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

class FakeTrafficModuleComponent : TrafficModuleComponent {

    override val state: Value<TrafficModuleFeature.State> = MutableValue(
        TrafficModuleFeature.State(
            TestDataDevices.testTrafficModuleInfo, isLoading = false, isError = false
        )
    )

    override fun onBack() {
        TODO(NOT_IMPLEMENT)
    }

    override fun onLookLogs() {
        TODO(NOT_IMPLEMENT)
    }

    override fun logsReceiver(scope: CoroutineScope): SharedFlow<Any> {
        TODO(NOT_IMPLEMENT)
    }

    override fun getDbLogs(): List<Any> {
        TODO(NOT_IMPLEMENT)
    }

    override fun getStatistics(): Any {
        TODO(NOT_IMPLEMENT)
    }

    override fun getStatus(): Any {
        TODO(NOT_IMPLEMENT)
    }

    companion object {
        private const val NOT_IMPLEMENT = "Not yet implemented"
    }
}
