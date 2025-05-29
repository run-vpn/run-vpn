package com.runvpn.app.android.vpn.ikev2

import android.os.Handler
import android.os.Looper
import java.util.Date
import java.util.Timer
import kotlin.concurrent.schedule

class TimerScheduler {

    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())
    private var terminated = false

    fun terminate() {
        terminated = true
        timer.cancel()
        handler.removeCallbacksAndMessages(null)
    }

    fun scheduleRTC(timeMs: Long, action: Runnable) {
        timer.schedule(Date(timeMs)) {
            if (!terminated)
                handler.post(action)
        }
    }
}
