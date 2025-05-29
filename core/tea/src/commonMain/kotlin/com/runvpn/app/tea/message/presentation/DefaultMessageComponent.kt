package com.runvpn.app.tea.message.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.message.domain.NullWrapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DefaultMessageComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService
) : MessageComponent, ComponentContext by componentContext {
    companion object {
        private const val DEFAULT_SHOW_TIME = 4_000L
    }

    private val componentScope = createCoroutineScope()
    private var autoDismissJob: Job? = null

    private val _visibleMessage: MutableValue<NullWrapper<AppMessage?>> = MutableValue(
        NullWrapper(null)
    )
    override val visibleMessage: Value<NullWrapper<AppMessage?>> = _visibleMessage

    init {
        lifecycle.doOnCreate {
            componentScope.launch {
                messageService.messageFlow.collect { msg ->
                    showMessage(msg)
                }
            }
        }
    }

    private fun showMessage(message: AppMessage) {
        autoDismissJob?.cancel()
        _visibleMessage.value = NullWrapper(message)
        if (message !is AppMessage.Persistent) {
            autoDismissJob = componentScope.launch {
                delay(DEFAULT_SHOW_TIME)
                _visibleMessage.value = NullWrapper(null)
            }
        }
    }

    override fun onActionClick() {
        autoDismissJob?.cancel()
        _visibleMessage.value.wrappedValue?.action?.invoke()
        _visibleMessage.value = NullWrapper(null)
    }
}
