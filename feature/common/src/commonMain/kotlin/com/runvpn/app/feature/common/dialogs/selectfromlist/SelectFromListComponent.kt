package com.runvpn.app.feature.common.dialogs.selectfromlist

import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.common.dialogs.DialogComponent

interface SelectFromListComponent<T> : DialogComponent {

    class State<T>(
        val title: String,
        val items: List<T>
    )

    val state: Value<State<T>>

    fun onItemClick(item: Int)

}
