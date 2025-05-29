package com.runvpn.app.tea.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

class Timer private constructor(
    private val startValue: Long,
    private val step: Long,
    private val type: TimerType
) {

    companion object {
        private const val DEFAULT_STEP_MILLIS = 1000L

        fun countdown(startValue: Long, step: Long = DEFAULT_STEP_MILLIS): Timer {
            return Timer(startValue, step, TimerType.Countdown)
        }

        fun stopwatch(stepMillis: Long = DEFAULT_STEP_MILLIS): Timer {
            return Timer(0L, stepMillis, TimerType.Stopwatch)
        }
    }

    enum class TimerType {
        Countdown,
        Stopwatch
    }

    private var timerJob: Job? = null

    private var _currentMillisFlow: MutableStateFlow<Long> = MutableStateFlow(startValue)
    val currentMillisFlow: StateFlow<Long> = _currentMillisFlow

    fun startTimer(
        coroutineScope: CoroutineScope,
        startTimerValue: Long? = null,
        timerType: TimerType? = null
    ) {
        timerJob?.cancel()

        timerJob = flow {
            var currentSeconds = startTimerValue ?: startValue
            val currentTimerType = timerType ?: type

            while (true) {
                if (currentTimerType == TimerType.Countdown) {
                    currentSeconds -= step
                } else {
                    currentSeconds += step
                }

                emit(currentSeconds)

                delay(step)
            }
        }
            .map {
                _currentMillisFlow.value = it

                if (it <= 0 && type == TimerType.Countdown) {
                    throw kotlinx.coroutines.CancellationException("cancelled")
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(coroutineScope)
    }

    fun stopTimer() {
        timerJob?.cancel()
        _currentMillisFlow.value = 0L
    }

    init {
        _currentMillisFlow.value = startValue
    }
}

