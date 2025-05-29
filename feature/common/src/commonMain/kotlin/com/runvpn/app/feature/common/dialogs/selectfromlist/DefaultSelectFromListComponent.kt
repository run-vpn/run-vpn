package com.runvpn.app.feature.common.dialogs.selectfromlist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class DefaultSelectFromListComponent<T>(
    componentContext: ComponentContext,
    title: String,
    items: List<T>,
    private val onItemSelected: (Int) -> Unit,
    private val onDismiss: () -> Unit
) : SelectFromListComponent<T>, ComponentContext by componentContext {


    private val _state = MutableValue(SelectFromListComponent.State(title = title, items = items))
    override val state: Value<SelectFromListComponent.State<T>> = _state

    override fun onItemClick(item: Int) {
        onItemSelected(item)
    }

    override fun onDismissClicked() {
        onDismiss()
    }

}
