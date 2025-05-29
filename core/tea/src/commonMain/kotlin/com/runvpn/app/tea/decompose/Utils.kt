package com.runvpn.app.tea.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

fun ComponentContext.createCoroutineScope(exceptionHandler: CoroutineExceptionHandler? = null): CoroutineScope {
    val oldScope = (instanceKeeper.get(ComponentScopeKey) as? CoroutineScopeWrapper)?.scope
    if (oldScope != null) return oldScope

    var context = SupervisorJob() + Dispatchers.Main
    if (exceptionHandler != null) context += exceptionHandler

    val scope = CoroutineScope(context)
    instanceKeeper.put(ComponentScopeKey, CoroutineScopeWrapper(scope))

    if (this.lifecycle.state != Lifecycle.State.DESTROYED) {
        this.lifecycle.doOnDestroy {
            scope.cancel()
        }
    } else {
        scope.cancel()
    }

    return scope
}

private object ComponentScopeKey

private class CoroutineScopeWrapper(val scope: CoroutineScope) : InstanceKeeper.Instance {
    override fun onDestroy() {
        // nothing
    }
}

