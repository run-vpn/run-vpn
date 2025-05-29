package com.runvpn.app.tea.utils

import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

fun CoroutineScope.safeLaunch(
    finallyBlock: (() -> Unit)? = null,
    block: suspend () -> Unit
): Job {
    return launch {
        try {
            block()
        } catch (e: CancellationException) {
            // do nothing
        } catch (e: Exception) {
            throw e
        } finally {
            finallyBlock?.invoke()
        }
    }
}

fun <T : Any> Value<T>.asStateFlow(): StateFlow<T> {
    val stateFlow = MutableStateFlow<T>(this.value)

    this.observe {
        stateFlow.value = it
    }

    return stateFlow
}
