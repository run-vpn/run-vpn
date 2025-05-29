package com.runvpn.app.feature.home

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.ConnectionStatisticsManager
import com.runvpn.app.data.device.data.models.update.FileInfo
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.entities.SuggestedServers
import com.runvpn.app.data.servers.utils.TestData.testServerList
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.data.settings.models.AppVersion
import com.runvpn.app.feature.common.dialogs.DialogComponent


class FakeHomeComponent(isConnected: Boolean = false, updateSuccess: Boolean = false) :
    HomeComponent {

    override val reviewDialog: Value<ChildSlot<*, DialogComponent>> =
        MutableValue(ChildSlot<Nothing, Nothing>(child = null))

    override val state: Value<HomeFeature.State> =
        MutableValue(
            HomeFeature.State(
                SuggestedServers(SuggestedServersMode.FAVORITES, testServerList),
                testServerList[0],
                isLoading = false,
                isDomainReachable = false,
                isError = false,
                vpnStatus = if (isConnected) ConnectionStatus.Connecting("SomeMessage")
                else ConnectionStatus.Disconnected,
                connectionTime = 0L,
                connectionErrorTime = 0L,
                connectionStats = ConnectionStatisticsManager.ConnectionStats(
                    0,
                    0,
                    0,
                    0,
                    0L,
                    ConnectionStatisticsManager.ConnectionStatsTimerType.Stopwatch
                ),
                vpnStatusHistory = listOf(
                    "Start connecting",
                    "Prepare XRay core",
                    "Setup",
                    "Connection"
                ),
                updateStatus = if (updateSuccess) UpdateStatus.Success(
                    "filePath",
                    UpdateInfo("", "", false, "", "1.0.16", FileInfo("", "", ""))
                ) else null,
                appVersion = AppVersion(1, "1.0.2")
            )
        )

    override fun onServerClicked(server: Server) {

    }

    override fun onCurrentServerClick() {

    }

    override fun onVpnButtonClick() {

    }

    override fun onShopClick() {

    }

    override fun onSubscribeInfoClick() {

    }

    override fun onConnectClick() {
        TODO("Not yet implemented")
    }

    override fun onDisconnectClick() {
        TODO("Not yet implemented")
    }

    override fun onPermissionsGranted() {
        TODO("Not yet implemented")
    }

    override fun offlineElementClicked() {
        TODO("Not yet implemented")
    }

}

