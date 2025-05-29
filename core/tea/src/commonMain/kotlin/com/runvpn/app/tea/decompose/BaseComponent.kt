package com.runvpn.app.tea.decompose

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.tea.runtime.coroutines.TeaRuntime
import com.runvpn.app.tea.runtime.coroutines.Update
import kotlinx.coroutines.CoroutineExceptionHandler

open class BaseComponent<State : Any, Message : Any, Dependencies : Any>(
    initialState: State,
    private val restore: (State) -> Update<State, Message, Dependencies>,
    private val update: (Message, State) -> Update<State, Message, Dependencies>,
    private val dependencies: Dependencies,
    private val enableLogs: Boolean = false,
    open val coroutineExceptionHandler: CoroutineExceptionHandler? = null
) {

    private val _state: MutableValue<State> = MutableValue(initialState)
    val state: Value<State> = _state



    private val runtime by lazy {
        val result = TeaRuntime(
            init = {
                val currentState = _state.value
                restore(currentState)
            },
            update = { m, s ->
                handleUpdateMessage(m)

                if (enableLogs) {
                    Logger.d(this::class.simpleName.toString()) { "Message: $m, State: $s" }
                }

                return@TeaRuntime update(m, s)
            },
            dependencies = dependencies,
            exceptionHandler = coroutineExceptionHandler
                ?: CoroutineExceptionHandler { _, throwable ->
                    Logger.e("CoroutineError", throwable)
                }
        )
        result.listenState { _state.value = it }
        result
    }

    open fun handleUpdateMessage(message: Message) {

    }

    fun dispatch(message: Message) = runtime.dispatch(message)
}

