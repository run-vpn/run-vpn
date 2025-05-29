package com.runvpn.app.feature.myservers

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.servers.utils.TestData

class FakeMyServersComponent : MyServersComponent {
    override val state: Value<MyServersComponent.State>
        get() = MutableValue(MyServersComponent.State(servers = TestData.testServerList))

    override fun onAddServerClick() {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }

    override fun onEditServerClick(uuid: String) {
        TODO("Not yet implemented")
    }

    override fun onDeleteServerClick(uuid: String) {
        TODO("Not yet implemented")
    }


}
