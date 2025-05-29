package com.runvpn.app.feature.myservers

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.Server

interface MyServersComponent {

    data class State(
        val servers: List<Server>
    )

    val state: Value<State>

    fun onAddServerClick()
    fun onBackClick()
    fun onEditServerClick(uuid: String)
    fun onDeleteServerClick(uuid: String)

    sealed interface Output {
        data object OnBack : Output
        data class OnAddServerScreenRequested(val serverToEdit: Server?) : Output
    }

}
